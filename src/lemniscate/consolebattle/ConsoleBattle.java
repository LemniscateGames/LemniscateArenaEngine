package lemniscate.consolebattle;

import lemniscate.data.ai.RandomChoiceAI;
import lemniscate.engine.battle.*;
import lemniscate.engine.data.FighterAI;
import lemniscate.engine.data.SkillData;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

// Static class to interact with local Battles through the terminal.
public class ConsoleBattle {
    private static final Scanner kb = new Scanner(System.in);

    // ==== MAIN METHODS
    public static void playBattleInConsole(Battle battle, FighterAI enemyAI){
        while (battle.isActive()){
            Fighter actor = battle.getNextActor();
            TurnRequest request;
            TurnResponse response = null;
            while (response == null || !response.accepted){
                if (actor.getTeam().getControllerId() == 1) {
                    request = consoleAct(actor);
                } else {
//                    displayBattle(battle);
                    request = enemyAI.decide(actor);
                }
//                System.out.println("request: "+request);
                response = battle.submitTurn(request);
//                System.out.println("response: "+response);
            }
            thiccDivider();
            displayTurn(response.turn);
        }

        displayBattle(battle);
        System.out.println("battle complete - total turns taken: "+battle.history.size());
        if (battle.getWinningTeam() != null){
            System.out.printf("winning team: %d - %s%n",
                    battle.getWinningTeam().getControllerId(),
                    battle.getWinningTeam().getFighters());
        }
    }
    public static void playBattleInConsole(Battle battle){
        playBattleInConsole(battle, new RandomChoiceAI());
    }

    /** Same as console battle but entirely automatic and don't log anything. **/
    public static void autoEvaluate(Battle battle, FighterAI enemyAI){
        while (battle.isActive()){
            // i love putting everything in one line!!!!
            battle.submitTurn(enemyAI.decide(battle.getNextActor()));
        }
    }

    // ==== INPUT
    /** Construct a turn decision for a fighter through the console. **/
    public static TurnRequest consoleAct(Fighter actor){
        // Loop until the actor successfully acts. Big ol nested loop time lesgooo
        // -- Select skill
        while (true) {
            displayBattle(actor.getBattle());
            Skill skill = choice(
                    Arrays.asList(actor.skills),
                    "Choose a skill",
                    s -> s.isUsable(actor) ? String.format("%s", s) : String.format("%s (%d)", s, s.getCurrentCooldown()),
                    s -> s.isUsable(actor),
                    s -> System.out.printf("%s cannot be used%n", s)
            );
            if (skill != null) {
                // -- Select target
                while (true) {
                    Fighter target = choice(skill.getPossibleTargets(actor), "Choose a target");
                    if (target == null) { break; } else {

                        // -- Choose to LE boost or not
                        int leBoost = yesNoCancelPrompt("LE Boost?");
                        if (leBoost != 0) {
                            boolean leBoosted = leBoost == 1;

                            // -- Submit the turn, if it is invalid then start all the way over
                            return new TurnRequest(actor, skill, target, leBoosted);
                        }
                    }
                }
            }
        }
    }

    public static <T> T choice(List<T> choices, String prompt,
                               Function<T, String> choiceDisplay,
                               Function<T, Boolean> validCheck,
                               Consumer<T> notUsable){
        for (int i=0; i<choices.size(); i++){
            T choice = choices.get(i);
            System.out.println((i+1)+". "+choiceDisplay.apply(choice));
        }
        while (true) {
            System.out.print(prompt+": ");
            int choiceIndex;
            try {
                choiceIndex = Integer.parseInt(kb.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
                continue;
            }
            if (choiceIndex == 0) return null;
            if (choiceIndex <= 0 || choiceIndex > choices.size()) {
                System.out.println("Number not in range");
            } else {
                T choice = choices.get(choiceIndex-1);
                if (validCheck.apply(choice)){
                    return choice;
                } else {
                    notUsable.accept(choice);
                }
            }
        }
    }

    public static <T> T choice(List<T> choices, String prompt){
        for (int i=0; i<choices.size(); i++){
            T choice = choices.get(i);
            System.out.printf("%d. %s%n", i+1, choice.toString());
        }
        while (true) {
            System.out.print(prompt+": ");
            int choiceIndex;
            try {
                choiceIndex = Integer.parseInt(kb.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
                continue;
            }
            if (choiceIndex == 0) return null;
            if (choiceIndex <= 0 || choiceIndex > choices.size()) {
                System.out.println("Number not in range");
            } else {
                return choices.get(choiceIndex-1);
            }
        }
    }

    public static boolean yesNoPrompt(String prompt){
        System.out.print(prompt+": ");
        String input = kb.nextLine();
        return (input.equalsIgnoreCase("y")
                || input.equalsIgnoreCase("yes")
                || input.equalsIgnoreCase("1"));
    }

    /** 1=yes, -1=no, 0=cancel **/
    public static int yesNoCancelPrompt(String prompt){
        System.out.print(prompt+" (0 to cancel):");
        String input = kb.nextLine();
        if (input.equalsIgnoreCase("0" )
                || input.equalsIgnoreCase("cancel")){
            return 0;
        }

        return (input.equalsIgnoreCase("y")
                || input.equalsIgnoreCase("yes")
                || input.equalsIgnoreCase("1"))
                ? 1 : -1;
    }

    // ==== DISPLAY STUFF
    public static void thiccDivider(){
        System.out.println("================================");
    }
    public static void divider(){
        System.out.println("--------------------------------");
    }

    public static void displayBattle(Battle battle){
        System.out.println();
        thiccDivider();
        for (Team team : battle.getTeams()){
            for (Fighter fighter : team.getFighters()){
                displayFighter(fighter);
            }
            divider();
        }
    }

    public static void displayFighter(Fighter fighter){
        System.out.printf("%s Lv.%d | %d/%d HP | %s | %s%n",
                fighter.getBattleName(),
                fighter.source.getLevel(),
                fighter.getHp(),
                fighter.getMaxHp(),
                fighter.statuses.stream().map(
                        status -> String.format("%s(%d)", status.data.name, status.getDuration())
                ).collect(Collectors.joining(", ")),
                fighter.getBattle().getNextActor() == fighter
                        ? "<--" :
                        SkillData.percent(fighter.getReadiness())
        );
    }

    public static void displayTurn(BattleTurn turn){
//        if (turn == null) {
//            System.out.println("Warning: invalid turn!");
//            return;
//        }
        for (TurnEvent event : turn.getEvents()){
            System.out.println(event.getMessage());
        }
    }
}
