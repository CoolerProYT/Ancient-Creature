package com.coolerpromc.ancientcreature.mixin;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.client.item.condition.DirtyCondition;
import com.coolerpromc.ancientcreature.client.item.select.FossilPartSelect;
import com.coolerpromc.ancientcreature.item.ModItems;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ClientItemInfoLoader.class)
public abstract class ClientItemInfoLoaderMixin {
    @Unique
    private static final String MODEL_DIRECTORY = "models/item/fossil_part";
    @Unique
    private static final String MODEL_PREFIX = "models/";
    @Unique
    private static final String MODEL_SUFFIX = ".json";
    @Unique
    private static final String DIRTY_PREFIX = "dirty_";

    @Inject(method = "scheduleLoad", at = @At("RETURN"), cancellable = true)
    private static void ancientCreature$injectFossilPartItemModel(ResourceManager manager, Executor executor, CallbackInfoReturnable<CompletableFuture<ClientItemInfoLoader.LoadedClientInfos>> cir) {
        CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> original = cir.getReturnValue();

        CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> modified = original.thenApplyAsync(loaded -> {
            ItemModel.Unbaked fossilModel = ancientCreature$createFossilPartModel(manager);
            loaded.contents().put(ModItems.FOSSIL_PART.id(), new ClientItem(fossilModel, ClientItem.Properties.DEFAULT));
            return loaded;
        }, executor);

        cir.setReturnValue(modified);
    }

    @Unique
    private static ItemModel.Unbaked ancientCreature$createFossilPartModel(ResourceManager resourceManager) {
        List<FossilModelEntry> models = ancientCreature$findFossilPartModels(resourceManager);

        List<SelectItemModel.SwitchCase<Identifier>> normalCases = new ArrayList<>();

        List<SelectItemModel.SwitchCase<Identifier>> dirtyCases = new ArrayList<>();

        for (FossilModelEntry entry : models) {
            SelectItemModel.SwitchCase<Identifier> switchCase = ItemModelUtils.when(entry.fossilPartId(), ItemModelUtils.plainModel(entry.modelId()));

            if (entry.dirty()) {
                dirtyCases.add(switchCase);
            } else {
                normalCases.add(switchCase);
            }
        }

        Constants.LOG.info("Discovered {} normal and {} dirty fossil-part models", normalCases.size(), dirtyCases.size());
        ItemModel.Unbaked normalSelect = ItemModelUtils.select(new FossilPartSelect(), normalCases);
        ItemModel.Unbaked dirtySelect = ItemModelUtils.select(new FossilPartSelect(), dirtyCases);

        return ItemModelUtils.conditional(new DirtyCondition(), dirtySelect, normalSelect);
    }

    @Unique
    private static List<FossilModelEntry> ancientCreature$findFossilPartModels(ResourceManager resourceManager) {
        Map<Identifier, Resource> resources = resourceManager.listResources(MODEL_DIRECTORY, id -> id.getPath().endsWith(MODEL_SUFFIX));

        List<FossilModelEntry> results = new ArrayList<>();

        for (Identifier resourceId : resources.keySet()) {
            FossilModelEntry entry = ancientCreature$parseModelResource(resourceId);

            if (entry != null) {
                results.add(entry);
            }
        }

        results.sort(Comparator.comparing((FossilModelEntry entry) -> entry.fossilPartId().toString()).thenComparing(FossilModelEntry::dirty));

        return results;
    }

    @Unique
    private static FossilModelEntry ancientCreature$parseModelResource(Identifier resourceId) {
        String resourcePath = resourceId.getPath();

        if (!resourcePath.startsWith(MODEL_PREFIX) || !resourcePath.endsWith(MODEL_SUFFIX)) {
            return null;
        }

        String modelPath = resourcePath.substring(MODEL_PREFIX.length(), resourcePath.length() - MODEL_SUFFIX.length());
        String expectedPrefix = "item/fossil_part/";

        if (!modelPath.startsWith(expectedPrefix)) {
            return null;
        }

        String fileName = modelPath.substring(expectedPrefix.length());

        if (fileName.isBlank() || fileName.contains("/")) {
            return null;
        }

        boolean dirty = fileName.startsWith(DIRTY_PREFIX);

        String fossilName = dirty ? fileName.substring(DIRTY_PREFIX.length()) : fileName;

        if (fossilName.isBlank()) {
            return null;
        }

        Identifier fossilPartId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), fossilName);

        Identifier modelId = Identifier.fromNamespaceAndPath(resourceId.getNamespace(), modelPath);

        return new FossilModelEntry(fossilPartId, modelId, dirty);
    }

    @Unique
    private record FossilModelEntry(Identifier fossilPartId, Identifier modelId, boolean dirty) {
    }
}