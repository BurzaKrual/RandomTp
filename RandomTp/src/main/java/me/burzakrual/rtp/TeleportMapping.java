package me.burzakrual.rtp;

public class TeleportMapping {
    public final String fromWorld;

    public final String destinationWorld;

    public final int centerX;

    public final int centerZ;

    public final int maxX;

    public final int maxZ;

    public TeleportMapping(String fromWorld, String destinationWorld, int centerX, int centerZ, int maxX, int maxZ) {
        this.fromWorld = fromWorld;
        this.destinationWorld = destinationWorld;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
    }
}
