package lemniscate.engine.data;

import lemniscate.engine.battle.Fighter;

public class ParameterDNEException extends Exception {
    public ParameterDNEException(Fighter fighter, String key) {
        super(String.format("Key \"%s\" does not exist on fighter %s",
                key, fighter.getData().name));
    }
}
