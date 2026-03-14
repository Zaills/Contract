package net.zaills.contract.component;

public enum Contract_Type {
    DeathExchange,
    Blocks,
    Regen;

    public static Contract_Type fromInt(int type){
        switch (type) {
            case 1 -> {
                return DeathExchange;
            }
            case 2 -> {
                return Blocks;
            }
            case 3 -> {
                return Regen;
            }
        }
        return null;
    }
}
