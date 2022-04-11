package lemniscate.engine.data;

/** Server-side data for fighters. (Not stored in database) **/
public abstract class FighterData {
    // ==== Fields
    /** String internal identifier.. **/
    public final String id;
    /** Display name. **/
    public final String name;
    /** Rarity (in stars). **/
    public final int rarity;
    /** Elemental type of this fighter. **/
    public final int elementalType;
    /** Class of this fighter. **/
    public final int fighterClass;

    /** Base stats. **/
    public final int baseHp, baseAtk, baseDef, baseSpd;

    /** Array of all skills. Length is 3 for fighters and varies for enemies. **/
    public final SkillData[] skills;

    /** Description of this fighter in the fighter menu. **/
    public final String description;

    // ==== Constructors
    public FighterData(String id, String name, int rarity, int elementalType, int fighterClass,
                       int hp, int atk, int def, int spd,
                       String description, SkillData... skills) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.elementalType = elementalType;
        this.fighterClass = fighterClass;
        this.baseHp = hp;
        this.baseAtk = atk;
        this.baseDef = def;
        this.baseSpd = spd;
        this.description = description;
        this.skills = skills;
    }

    /** Add all required parameters to the passed SkillParams objects
     * for skills to reference on the Fighter this SkillParams object is instantiated in later. **/
    public abstract void initializeParams(SkillParams params, int[] levels);
}
