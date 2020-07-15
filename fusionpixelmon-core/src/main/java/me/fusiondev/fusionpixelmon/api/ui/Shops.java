package me.fusiondev.fusionpixelmon.api.ui;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import me.fusiondev.fusionpixelmon.FusionPixelmon;
import me.fusiondev.fusionpixelmon.api.AbstractConfig;
import me.fusiondev.fusionpixelmon.api.AbstractPlayer;
import me.fusiondev.fusionpixelmon.api.colour.DyeColor;
import me.fusiondev.fusionpixelmon.api.economy.IEconomyProvider;
import me.fusiondev.fusionpixelmon.api.inventory.InvInventory;
import me.fusiondev.fusionpixelmon.api.inventory.InvPage;
import me.fusiondev.fusionpixelmon.api.items.AbstractItemStack;
import me.fusiondev.fusionpixelmon.modules.pokedesigner.ui.*;

import java.util.*;

public abstract class Shops {

    /**
     * The player currently using the Shop
     */
    protected AbstractPlayer player;

    /**
     * The pokemon the player is currently editing.
     */
    public Pokemon pokemon;

    /**
     * The inventory object for the currently open inventory.
     */
    protected InvInventory inv;

    /**
     * The ID of the shop inventory page.
     */
    protected final String SHOP_ID = "pokeeditor";

    protected List<InvPage> pages;// todo check if this is even needed

    /**
     * The inventory GUI page for the main shop.
     */
//    InvPage pagePokeEditor;// todo can be converted into local variable

    /**
     * The player's bank account.
     */
    protected IEconomyProvider bank;

    public Shops(AbstractPlayer player) {
        this.player = player;
        if (!playerSelectedOptions.containsKey(player.getUniqueId())) {
            playerSelectedOptions.put(player.getUniqueId(), new HashMap<>());
        }
    }

    // also depends on which poke slot!/which pokemon is selected
    /**
     * Stores the selected option/value pairs, for each concurrent player.
     */
    public static HashMap<UUID, HashMap<Options, Object>> playerSelectedOptions = new HashMap<>();

    /**
     * Gets the option/value pairs the current player has selected.
     *
     * @return the option/value pairs the current player has selected.
     */
    public HashMap<Options, Object> getSelectedOptions() {
        return playerSelectedOptions.get(player.getUniqueId());
    }

    /**
     * Resets (deletes and creates new empty store for) the current player's selected
     * option/value pairs, and selectively closes the player's inventory.
     *
     * @param closeInv whether the player's inventory should be closed (exiting GUI).
     */
    public void resetSelectedOptions(boolean closeInv) {
        resetSelectedOptions(player, closeInv);
    }

    /**
     * Resets (deletes and creates new empty store for) the specified player's selected
     * option/value pairs, and selectively closes the player's inventory.
     *
     * @param player   the player.
     * @param closeInv whether the player's inventory should be closed (exiting GUI).
     */
    public static void resetSelectedOptions(AbstractPlayer player, boolean closeInv) {
        playerSelectedOptions.remove(player.getUniqueId());
        playerSelectedOptions.put(player.getUniqueId(), new HashMap<>());// todo can only initialize if needed
        if (closeInv) player.closeInventory();
    }


    /**
     * Maps the {@link Options} objects to their {@link BaseShop shop} implementations.
     */
    public HashMap<Options, BaseShop> shops = new HashMap<>();

    /**
     * Creates and adds the shops.
     */
    protected abstract void initShops();

    /**
     * Lists the different shop options with their properties to show in the main shop.
     */
    public enum Options {
        LEVEL(11, "Level", "level", LevelShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("rare_candy")),
        ABILITY(13, "Ability", "ability", AbilityShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("ability_capsule")),
        NATURE(15, "Nature", "nature", NatureShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("ever_stone")),
        IVEV(20, "IVs/EVs", "IVs/EVs", IVEVShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("destiny_knot")),
        GENDER(22, "Gender", "gender", GenderShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("full_incense")),
        GROWTH(24, "Growth", "growth", GrowthShop.class, FusionPixelmon.getRegistry().getItemTypesRegistry().DYE().to().setColour(DyeColor.WHITE)),
        SHINY(29, "Shiny", "shininess", ShinyShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("light_ball")),
        POKEBALL(31, "Pokeball", "pokeball", PokeballShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("poke_ball")),
        FORM(33, "Form", "form", FormShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("meteorite")),
        EVOLUTION(4, "Evolution", "evolution", EvolutionShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("fire_stone")),
        NICK(2, "Nick", "nick colour and style", NickShop.class, FusionPixelmon.getRegistry().getPixelmonUtils().getPixelmonItemStack("ruby"));

        private int slot;
        private String name;
        private String modifyWhat;
        private Class<? extends BaseShop> shopClass;
        private AbstractItemStack itemStack;

        Options(int slot, String name, String modifyWhat, Class<? extends BaseShop> shopClass, AbstractItemStack itemStack) {
            this.slot = slot;
            this.name = name;
            this.modifyWhat = modifyWhat;
            this.shopClass = shopClass;
            this.itemStack = itemStack;
        }

        public int getSlot() {
            return slot;
        }

        public String getName() {
            return name;
        }

        public String getModifyWhat() {
            return modifyWhat;
        }

        public Class<? extends BaseShop> getShopClass() {
            return shopClass;
        }

        public AbstractItemStack getItemStack() {
            return itemStack;
        }
    }


    /**
     * Calculates the total cost of all the options that the player has selected.
     *
     * @return the total cost of the options the player has selected.
     */
    public int calculateCost() {
        int totalCost = 0;
        for (Map.Entry<Options, Object> entry : getSelectedOptions().entrySet()) {
            if (!shops.containsKey(entry.getKey())) continue;
            totalCost += shops.get(entry.getKey()).prices(entry.getValue());
        }
        return totalCost;
    }

    /**
     * Launch the Shop GUI to display options, other shops, etc.
     *
     * @param pokemon the selected pokemon.
     */
    public abstract void launch(Pokemon pokemon, String guiTitle);


    /**
     * Gets the config object of the Shop.
     *
     * @return the config object of the shop.
     */
    public abstract AbstractConfig getShopConfig(Options option);

    /**
     * Gets the price of the specified key from the shop config, or the specified
     * default price if cannot.
     *
     * @param key          the config key.
     * @param defaultPrice the default price.
     * @return the price of the key from the shop config; or the defaultPrice if cant.
     */
    public abstract int getPriceOf(Options option, String key, int defaultPrice);
}
