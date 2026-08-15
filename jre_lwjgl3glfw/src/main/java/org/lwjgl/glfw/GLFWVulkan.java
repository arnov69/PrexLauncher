package org.lwjgl.glfw;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.FunctionProvider;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

/**
 * Java-only stub for GLFWVulkan, matching the signatures of the real LWJGL class.
 *
 * This launcher's rendering backends (GL4ES, MobileGlues, Zink, VirGL...) are all
 * OpenGL-based, so Vulkan is always reported as unsupported. Minecraft 26.x probes
 * Vulkan support during startup (VulkanBackend.checkBackendAvailable) — returning
 * false here makes it fail gracefully and fall back to the OpenGL backend instead
 * of crashing on a missing native GLFW library.
 */
public class GLFWVulkan {

    public static void glfwInitVulkanLoader(long loader) {
        // No-op: Vulkan is not supported by this launcher.
    }

    public static boolean glfwVulkanSupported() {
        return false;
    }

    public static PointerBuffer glfwGetRequiredInstanceExtensions() {
        return null;
    }

    public static long glfwGetInstanceProcAddress(VkInstance instance, ByteBuffer name) {
        return 0L;
    }

    public static long glfwGetInstanceProcAddress(VkInstance instance, CharSequence name) {
        return 0L;
    }

    public static boolean glfwGetPhysicalDevicePresentationSupport(VkInstance instance, VkPhysicalDevice device, int queuefamily) {
        return false;
    }

    public static int glfwCreateWindowSurface(VkInstance instance, long window, VkAllocationCallbacks allocator, LongBuffer surface) {
        return GLFW.GLFW_API_UNAVAILABLE;
    }

    public static int glfwCreateWindowSurface(VkInstance instance, long window, VkAllocationCallbacks allocator, long[] surface) {
        return GLFW.GLFW_API_UNAVAILABLE;
    }

    public static void setPath(FunctionProvider provider) {
        // No-op: Vulkan is not supported by this launcher.
    }

    public static void setPath(String libraryPath) {
        // No-op: Vulkan is not supported by this launcher.
    }
}
