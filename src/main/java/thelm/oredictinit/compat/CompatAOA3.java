package thelm.oredictinit.compat;

import static thelm.oredictinit.registry.OreDictRegisCore.getBlock;
import static thelm.oredictinit.registry.OreDictRegisCore.getItem;

import net.minecraftforge.oredict.OreDictionary;
import thelm.oredictinit.api.ICompat;

import java.util.HashMap;

public class CompatAOA3 implements ICompat {

    private static final HashMap<String,String> aoaStuff = new HashMap<>();

    @Override
    public String getName() {
        return "adventofascension";
    }

    @Override
    public void register() {
        aoaStuff.put("Baronyte","ingot");
        aoaStuff.put("Blazium","ingot");
        aoaStuff.put("Bloodstone","gem");
        aoaStuff.put("Crystallite","gem");
        aoaStuff.put("Elecanium","ingot");
        aoaStuff.put("Emberstone","ingot");
        aoaStuff.put("Gemenyte","gem");
        aoaStuff.put("Ghastly","ingot");
        aoaStuff.put("Ghoulish","ingot");
        aoaStuff.put("Jade","gem");
        aoaStuff.put("Amethyst","gem");
        aoaStuff.put("Jewelyte","gem");
        aoaStuff.put("Limonite","ingot");
        aoaStuff.put("Lyon","ingot");
        aoaStuff.put("Mystite","ingot");
        aoaStuff.put("Ornamyte","gem");
        aoaStuff.put("Rosite","ingot");
        aoaStuff.put("Sapphire","gem");
        aoaStuff.put("Shyregem","gem");
        aoaStuff.put("Shyrestone","ingot");
        aoaStuff.put("Varsium","ingot");

        for (String material : aoaStuff.keySet()) {
            if (aoaStuff.get(material).equals("ingot")) {
                OreDictionary.registerOre("ingot"+material,getItem("aoa3",material.toLowerCase()+"_ingot"));
            }
            else {
                OreDictionary.registerOre("gem"+material,getItem("aoa3",material.toLowerCase()));
            }
            OreDictionary.registerOre("ore"+material,getBlock("aoa3",material.toLowerCase()+"_ore"));
        }
    }
}