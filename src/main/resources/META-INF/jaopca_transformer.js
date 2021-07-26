var ASM = Java.type("net.minecraftforge.coremod.api.ASMAPI");
var Opcodes = Java.type("org.objectweb.asm.Opcodes");
var VarInsnNode = Java.type("org.objectweb.asm.tree.VarInsnNode");;
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");

function initializeCoreMod() {
    return {
        "data_pack_hook": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.server.MinecraftServer",
                "methodName": "configurePackRepository",
                "methodDesc": "(Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/world/level/DataPackConfig;Z)Lnet/minecraft/world/level/DataPackConfig;"
            },
            "transformer": function(method) {
                var newInstructions = new InsnList();

                newInstructions.add(ASM.buildMethodCall(
                    "thelm/jaopca/events/CommonEventHandler",
                    "getInstance",
                    "()Lthelm/jaopca/events/CommonEventHandler;",
                    ASM.MethodType.STATIC));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(ASM.buildMethodCall(
                    "thelm/jaopca/events/CommonEventHandler",
                    "onDataPackDiscovery",
                    "(Lnet/minecraft/server/packs/repository/PackRepository;)V",
                    ASM.MethodType.VIRTUAL));
                
                var node = ASM.findFirstMethodCall(
                    method,
                    ASM.MethodType.STATIC,
                    "net/minecraftforge/fmllegacy/packs/ResourcePackLoader",
                    "loadResourcePacks",
                    "(Lnet/minecraft/server/packs/repository/PackRepository;Ljava/util/function/BiFunction;)V");
                method.instructions.insert(node, newInstructions);
                return method;
            }
        },
        // This needs to be made back into a mixin when it becomes available
        // because this is where KubeJS adds its recipe mixin and things might break
        "recipe_hook": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.world.item.crafting.RecipeManager",
                "methodName": "apply",
                "methodDesc": "(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"
            },
            "transformer": function(method) {
                var newInstructions = new InsnList();

                newInstructions.add(ASM.buildMethodCall(
                    "thelm/jaopca/events/CommonEventHandler",
                    "getInstance",
                    "()Lthelm/jaopca/events/CommonEventHandler;",
                    ASM.MethodType.STATIC));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(ASM.buildMethodCall(
                    "thelm/jaopca/events/CommonEventHandler",
                    "onReadRecipes",
                    "(Ljava/util/Map;)V",
                    ASM.MethodType.VIRTUAL));

                method.instructions.insert(newInstructions);
                return method;
            }
        }
    }
}