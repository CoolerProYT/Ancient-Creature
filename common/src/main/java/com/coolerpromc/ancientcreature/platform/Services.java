package com.coolerpromc.ancientcreature.platform;

import com.coolerpromc.ancientcreature.Constants;
import com.coolerpromc.ancientcreature.platform.services.ICapabilityHelper;
import com.coolerpromc.ancientcreature.platform.services.IMenuHelper;
import com.coolerpromc.ancientcreature.platform.services.IPlatformHelper;
import com.coolerpromc.ancientcreature.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);
    public static final IMenuHelper MENU = load(IMenuHelper.class);
    public static final ICapabilityHelper CAPABILITIES = load(ICapabilityHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader()).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}