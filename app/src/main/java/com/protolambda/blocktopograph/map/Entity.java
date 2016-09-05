package com.protolambda.blocktopograph.map;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
Entity enum for MCPE -- by @protolambda

--- Please attribute @protolambda for generating+updating this enum
 */
public enum Entity {

    CHICKEN(10, "Chicken", "Chicken", "chicken", 4),
    COW(11, "Cow", "Cow", "cow", 3),
    PIG(12, "Pig", "Pig", "pig", 1),
    SHEEP(13, "Sheep", "Sheep", "sheep", 2),
    WOLF(14, "Wolf", "Wolf", "wolf", 18),
    VILLAGER(15, "Villager", "Villager", "villager", 7),
    MUSHROOM_COW(16, "Mooshroom", "MushroomCow", "mooshroom", 6),
    SQUID(17, "Squid", "Squid", "squid", 5),
    RABBIT(18, "Rabbit", "Rabbit", "rabbit", 89),
    BAT(19, "Bat", "Bat", "bat", 64),
    VILLAGER_GOLEM(20, "Iron Golem", "VillagerGolem", "iron-golem", 48),
    SNOW_MAN(21, "Snow Golem", "SnowMan", "snow-golem", 31),
    OZELOT(22, "Ocelot", "Ozelot", "ozelot", 37),
    HORSE(23, "Donkey", "EntityHorse", "horse", 73),
    HORSE_DONKEY(24, "Donkey", "EntityHorse", "donkey", 74),
    HORSE_MULE(25, "Donkey", "EntityHorse", "mule", 75),
    HORSE_SKELETON(26, "Donkey", "EntityHorse", "skeleton-horse", 76),
    HORSE_ZOMBIE(27, "Donkey", "EntityHorse", "zombie-horse", 77),
    //28
    //29
    //ARMOR_STAND(30, "Armor Stand", "ArmorStand", TODO, -1),//TODO not sure
    //GIANT(31, "Giant Zombie", "Giant", "giant", 9),//TODO not sure
    ZOMBIE(32, "Zombie", "Zombie", "zombie", 9),
    CREEPER(33, "Creeper", "Creeper", "creeper", 14),
    SKELETON(34, "Skeleton", "Skeleton", "skeleton", 10),
    SPIDER(35, "Spider", "Spider", "spider", 11),
    PIG_ZOMBIE(36, "Zombie Pigman", "PigZombie", "zombie-pigman", 17),
    SLIME(37, "Slime", "Slime", "slime", 15),
    ENDERMAN(38, "Enderman", "Enderman", "enderman", 21),
    SILVERFISH(39, "Silverfish", "Silverfish", "silverfish", 22),
    CAVE_SPIDER(40, "Cave Spider", "CaveSpider", "cave-spider", 13),
    GHAST(41, "Ghast", "Ghast", "ghast", 16),
    LAVA_SLIME(42, "Magma Cube", "LavaSlime", "magma-cube", 24),
    BLAZE(43, "Blaze", "Blaze", "blaze", 32),
    //44
    WITCH(45, "Witch", "Witch", "witch", 54),
    SKELETON_STRAY(46, "Stray", "Skeleton", "stray", 98),
    ZOMBIE_HUSK(47, "Husk", "Zombie", "husk", 99),
    SKELETON_WITHER(48, "Wither Skeleton", "Skeleton", "wither", 61),
    //49
    //50
    //51
    //52
    //53
    //54
    //55
    //56
    //57
    //58
    //59
    //60
    //61
    CAMERA(62, "Tripod Camera", "TripodCamera", "camera", -1),//TODO
    PLAYER(63, "Player", "Player", "player", 8),
    ITEM(64, "Dropped Item", "ItemEntity", "item", -1),//TODO
    PRIMED_TNT(65, "Primed TNT", "PrimedTnt", "primed-tnt", 49),
    FALLING_SAND(66, "Falling Block", "FallingBlock", "falling-sand", 50),
    //67
    THROWN_EXP_BOTTLE(68, "Bottle o' Enchanting", "ThrownExpBottle", "ThrownExpBottle", 56),
    XP_ORB(69, "Experience Orb", "XPOrb", "experience-orb", 59),
    //70-79
    ARROW(80, "Arrow", "Arrow", "arrow", 41),
    SNOWBALL(81, "Snowball", "Snowball", "snowball", 42),
    PAINTING(83, "Painting", "Painting", "painting", 65),
    MINECART_RIDEABLE(84, "Minecart", "MinecartRideable", "minecart", 34),
    FIREBALL(85, "Ghast Fireball", "Fireball", "fireball", 44),
    THROWN_POTION(86, "Splash Potion", "ThrownPotion", "ThrownPotion", 95),
    THROWN_ENDERPEARL(87, "Ender Pearl", "ThrownEnderpearl", "ender-pearl", 46),
    LEASH_KNOT(88, "Lead Knot", "LeashKnot", "lead-knot", 94),
    //89
    BOAT(90, "Boat", "Boat", "boat", 33),
    //91
    //92
    LIGHTNING(93, "Lightning Bolt", "LightningBolt", "lightning", 58),
    SMALL_FIREBALL(94, "Blaze Fireball", "SmallFireball", "fireball", 44),
    //95
    MINECART_HOPPER(96, "Minecart with Hopper", "MinecartHopper", "minecart-with-hopper", 70),
    MINECART_TNT(97, "Minecart with TNT", "MinecartTNT", "minecart-with-tnt", 69),
    MINECART_CHEST(98, "Storage Minecart", "MinecartChest", "minecart-chest", 35),
    //MINECART_SPAWNER(99, "Minecart with Spawner", "MinecartSpawner", "minecart-with-spawner", 71),//TODO not sure
    //MINECART_COMMAND_BLOCK(100, "Minecart with Command Block", "MinecartCommandBlock", "minecart-with-command-block", 78),//TODO not sure
    //MINECART_FURNACE(101, "Powered Minecart", "MinecartFurnace", "minecart-furnace", 36),//TODO not sure

    //===== TODO not yet in game ======
    //EYE_OF_ENDER_SIGNAL(191, "Eye of Ender", "EyeOfEnderSignal", "eye-of-ender", 47),
    //ITEM_FRAME(192, "Item Frame", "ItemFrame", "empty-item-frame", 66),
    //WITHER_SKULL(193, "Wither Skull", "WitherSkull", "wither-skull", 60),
    //FIREWORKS_ROCKET_ENTITY(194, "Firework Rocket", "FireworksRocketEntity", TODO, -1),
    //ENDER_DRAGON(195, "Ender Dragon", "EnderDragon", "ender-dragon", 29),
    //WITHER_BOSS(196, "Wither", "WitherBoss", "blue-wither-skull", 72),
    //ENDERMITE(197, "Endermite", "Endermite", "endermite", 86),
    //GUARDIAN(198, "Guardian", "Guardian", "guardian", 87),
    //SHULKER(199, "Shulker", "Shulker", "shulker", 30),
    //ENDER_CRYSTAL(200, "Ender Crystal", "EnderCrystal", "ender-crystal", 52)

    UNKNOWN(999, "Unknown", "Unknown", "unknown", 143);

    public final int id, sheetPos;
    public final String displayName, dataName, wikiName;

    public Bitmap bitmap;

    Entity(int id, String displayName, String dataName, String wikiName, int sheetPos){
        this.id = id;
        this.displayName = displayName;
        this.dataName = dataName;
        this.wikiName = wikiName;
        this.sheetPos = sheetPos;
    }

    private static final Map<String, Entity> entityMap;
    private static final Map<Integer, Entity> entityByID;

    static {
        entityMap = new HashMap<>();
        entityByID = new HashMap<>();
        for(Entity e : Entity.values()){
            entityMap.put(e.dataName, e);
            entityByID.put(e.id, e);
        }
    }

    /* TODO URGENT 0.16; make more robust, use fallbacks!
        Mojang is changing names in PC version as well as mcpe v0.16, expect things to break.
     */
    public static Entity getEntity(String dataName){
        return entityMap.get(dataName);
    }

    public static Entity getEntity(int id){
        return entityByID.get(id);
    }

    public static void loadEntityBitmaps(AssetManager assetManager) throws IOException {
        Bitmap sheet = BitmapFactory.decodeStream(assetManager.open("entity_wiki.png"));
        int w = sheet.getWidth();
        int tileSize = 16;
        for(Entity e : Entity.values()){
            if(e.bitmap == null && e.sheetPos >= 0){
                int p = (e.sheetPos-1) * tileSize;
                int x = p % w;
                int y = ((p - x) / w) * tileSize;
                //read tile from sheet, scale to 32x32
                e.bitmap = Bitmap.createScaledBitmap(Bitmap.createBitmap(sheet, x, y, tileSize, tileSize, null, false), 32, 32, false);
            }
        }
    }

}
