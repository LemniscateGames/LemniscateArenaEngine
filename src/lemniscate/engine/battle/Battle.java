package lemniscate.engine.battle;

import lemniscate.engine.Utils;
import lemniscate.engine.battle.actions.ActAction;
import lemniscate.engine.battle.actions.TurnAction;
import lemniscate.engine.battle.results.GeneralResult;
import lemniscate.engine.battle.results.TurnEventMessage;

import java.util.*;

/** A server-side Battle instance. **/
public class Battle {
    /** The RNG object used to create the seeds for the battles themselves. (Static to the battle class) **/
    private static final Random seedGenerator = new Random();

    /** All teams participating in this battle. **/
    public final Team[] teams;
    /** The seed used to generate the RNG for this specific battle. **/
    public final long seed;
    /** All random calls in the effects of moves should be made with this RNG object. **/
    public final Random rng;

    /** A list of all actions taken by fighters during battle. **/
    public final ArrayList<BattleTurn> history;

    /** The turn currently running. TurnEvents across the code are tracked into here. **/
    private BattleTurn turn;

    /** The next fighter to act. **/
    private Fighter nextActor;

    /** Whether this battle is still active (no team has won yet). **/
    private boolean active;

    /** After this battle has ended, this is the winning team of the battle. **/
    private Team winningTeam;

    // Constructors
    private static final char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    public Battle(Team[] teams, long seed) {
        this.teams = teams;
        this.seed = seed;
        rng = new Random(this.seed);
        history = new ArrayList<>();
        active = true;

        // Set this battle to the battle field of all fighters in this battle and initialize battle names
        // (Idk why this section of the code is so long, i guess im bad at using maps)
        // Keep track of the total amount of each name
        Map<String, Integer> nameCounts = new HashMap<>();
        List<Fighter> fighters = allFighters();
        for (Fighter fighter : fighters){
            fighter.setBattle(this);
            String name = fighter.getData().name;
            if (nameCounts.containsKey(name)){
                nameCounts.put(name, nameCounts.get(name)+1);
            } else {
                nameCounts.put(name, 1);
            }
        }

        // Create a new map with each of the counted names as 0 to keep track of current amount
        Map<String, Integer> currentCounts = new HashMap<>();
        for (String name : nameCounts.keySet()){
            currentCounts.put(name, 0);
        }

        // Assign letters in order of how they appear in the fighters list.
        for (Fighter fighter : fighters){
            String name = fighter.getData().name;
            if (nameCounts.get(name) > 1){
                fighter.setBattleName(name + " " + letters[currentCounts.get(name)]);
                currentCounts.put(name, currentCounts.get(name)+1);
            } else {
                fighter.setBattleName(name);
            }
        }

        // Advance time so that the first fighter to act is first up
        advanceTime();
    }

    public Battle(Team[] teams){
        this(teams, seedGenerator.nextLong());
    }

    public Battle(Team allies, Team enemies, long seed) {
        this(new Team[]{allies, enemies}, seed);
    }

    public Battle(Team allies, Team enemies) {
        this(new Team[]{allies, enemies});
    }

    /** Check if there is one team with living fighters remaining.
     * Render this battle inactive if so, meaning new turns will not start or be accepted.
     * The turn currently running will continue though.
     * At the moment there is only one team with living fighters remaining, the winner is decided but
     * effects may continue to be run, which may result in all the winning team's fighters being defeated.
     * There will never be zero teams alive without passing through one alive, because fighters can only
     * "die" one at a time.
     */
    public void check(){
        // If already inactive don't bother checking
        if (!active) return;

        // Find if there is exactly one team with at least one living ally
        // (which there should be at least once during a battle)
        List<Team> livingTeams = new ArrayList<>();
        for (Team team : teams){
            if (team.hasLivingMembers()) livingTeams.add(team);
            if (livingTeams.size() > 1) return;
        }

        if (livingTeams.size() == 1){
            winningTeam = livingTeams.get(0);
            active = false;
            nextActor = null;
        }
    }

    /** Get all fighters in this battle across all teams. **/
    public List<Fighter> allFighters(){
        ArrayList<Fighter> fighters = new ArrayList<>();
        for (Team team : teams){
            fighters.addAll(team.getFighters());
        }
        return fighters;
    }
    /** Get all alive fighters in this battle. **/
    public List<Fighter> livingFighters(){
        List<Fighter> fighters = allFighters();
        fighters.removeIf(f -> !f.isAlive());
        return fighters;
    }

    /** Advance the readiness of all fighters and make the next fighter to act ready. **/
    public void advanceTime(){
        // While the next actor's turn was not cancelled, make the next actor up next to act.
        TurnAction action = null;
        while (active && (action == null || !action.isActive())){

            // If this battle has been going on for more than 500 turns,
            // stop the battle because there's probably a loop happening
            if (history.size() > 500){
                active = false;
            }

            // Advance readiness and get the next actor up to 100%, then set them to 0% and start their turn.
            List<Fighter> fighters = livingFighters();
            Optional<Fighter> nextActorFound = fighters.stream().min(Fighter.actOrderSort);
            if (nextActorFound.isPresent()){
                nextActor = nextActorFound.get();
                double time = nextActor.timeUntilReady();
                for (Fighter fighter : fighters){
                    fighter.passTime(time);
                }
            } else {
                System.out.println("next actor kinda not found...?");
            }
            nextActor.setReadiness(0);

            // Set up the new turn
            turn = new BattleTurn(nextActor);
            history.add(turn);

            // Broadcast the new turn. If cancelled, this fighter will not be the final nextActor
            // and the next actor will be queued through this process.
            action = new TurnAction(nextActor);
            nextActor.broadcast(action);

            // INVOKE: turn start
            nextActor.invoke(Trigger.TURN_START);
        }
        // By here, a fighter that is ready to act should be the nextActor.
    }

    /** Submit a turn request for the current nextActor and evaluate it if the request is valid.
     * Returns false if the request has invalid parameters. **/
    public TurnResponse submitTurn(TurnRequest request){
        // Check if this battle has already ended.
        if (!active) return new TurnResponse("This battle is already over.");

        // Just a check to prevent a request from being sent to the wrong fighter,
        // which probably won't happen with how the game is coded to not accept requests until the previous one is finished,
        // but just in case.
        if (request.actor != nextActor) return new TurnResponse(
                String.format("Wrong actor! (It is %s's turn)",
                        nextActor.getBattleName()));

        // Check that the actor can use the skill.
        if (!request.getSkill().isUsable(nextActor)) return new TurnResponse(
                String.format("%s cannot use %s right now",
                        nextActor.getBattleName(), request.getSkill().getName()));



        // Check that the target that the player selected can be targeted by the skill.
        if (!request.getSkill().data.targetType
                .getPossibleTargets(nextActor)
                .contains(request.getTarget())
        ) return new TurnResponse(
                String.format("%s cannot target %s with %s",
                        nextActor.getBattleName(),
                        request.getTarget().getBattleName(),
                        request.getSkill().getName()));

        // LE is stored as one int value = one tenth LE, but the skill LE costs are stored as actual LE cost,
        // so that's why it's multiplied by 10 here
        if (request.isLeBoosted() && nextActor.getTeam().getLe() < request.getSkill().data.leBoostCost*10) return new TurnResponse(
                "Not enough LE to LE-boost");

        // --> If the code reaches here, the request is valid <--
        // Fill out the turn with data of what was chosen for this turn
        // The 'turn' field will change when advanceReadiness() is called, so store the current turn in thisTurn
        // For some reason, 'nextActor' might also change so store that too
        BattleTurn thisTurn = turn;
        thisTurn.setValues(request);
        Fighter actor = nextActor;

        // Broadcast the actAction which may change the target selected or other shits
        actor.broadcast(new ActAction(actor, request));

        // Use the skill
        actor.use(request.getSkill(), request.getTarget(), request.isLeBoosted());

        // INVOKE: turn end
        actor.invoke(Trigger.TURN_END);

        // Advance time, making the next actor to act up next,
        // and now this submitTurn method will only accept requests to that actor.
        advanceTime();

        // Return a response, with a brief description of the turn because why not
        return new TurnResponse(true, String.format("%s used %s on %s%s",
                actor.getBattleName(), request.getSkill().getName(), actor.getTargets(),
                request.isLeBoosted() ? " (LE-boosted!)" : ""),
                thisTurn);
    }

    public TurnResponse submitTurn(Fighter actor, Skill skill, Fighter target, boolean leBoosted){
        return submitTurn(new TurnRequest(actor, skill, target, leBoosted));
    }

    // ======== RNG
    /** Have a random chance to do something.
     * NOTE!! This should only be used when evaluating effects!
     * If it is called elsewhere then it will throw off the seed generation and random chances could be different
     * when re-evaluating the battle.
     * @param p the chance percentage to return true, from 0.0 to 1.0
     * @return true if rng passed, false if rng failed
     */
    public boolean ifChance(double p){
        return rng.nextDouble() < p;
    }

    public <T> T randomChoice(T[] items){
        return Utils.randomChoice(rng, items);
    }
    public <T> T randomChoice(List<T> items){
        return Utils.randomChoice(rng, items);
    }

    /** Add an event to the current turn. **/
    public void addEvent(TurnEvent event){
        event.storeMessage();
        turn.addEvent(event);
    }
    public void addMessage(String message){
        addEvent(new TurnEventMessage(message));
    }

    public void addGeneralResult(String id, Fighter fighter, int value, String message){
        addEvent(new GeneralResult(id, fighter, value, message));
    }

    // ======== ACCESSORS
    public BattleTurn getTurn() {
        return turn;
    }

    public Team[] getTeams() {
        return teams;
    }

    public boolean isActive() {
        return active;
    }

    public Fighter getNextActor() {
        return nextActor;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }
}
