package sexy.poke;

import com.google.common.util.concurrent.AtomicDouble;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import sexy.poke.transformers.TransformTileEntityRendererDispatcher;
import sexy.poke.transformers.TransformUpdate;
import com.jadarstudios.developercapes.DevCapes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mod(modid = "pokepatch", version = "1.0")
public class Pokepatch {

    public static final String MODID = "pokepatch";
    public static final String VERSION = "1.0";

    private static boolean reloaded = false;

    public static int[] blacklistedLightDims = new int[]{};
    
    Logger logger = LogManager.getLogger(MODID);

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        long time = System.currentTimeMillis();
        TransformUpdate.map.entrySet().removeIf(it -> it.getValue() + 3000L < time);


        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)) {
            if (!wasPressed) {
                showBlockUpdates = !showBlockUpdates;
                wasPressed = true;
            }
        } else {
            wasPressed = false;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
            if (!wasPressedTileInfo) {
                showTileInfo = !showTileInfo;
                wasPressedTileInfo = true;
            }
        } else {
            wasPressedTileInfo = false;
        }
    }

    public static boolean showBlockUpdates = false;
    static boolean wasPressed;

    public static boolean showTileInfo = false;
    static boolean wasPressedTileInfo;

    public static void render() {

        //fix blackscreen
        if (!reloaded) {
            reloaded = true;

            Minecraft mc = Minecraft.getMinecraft();
            Minecraft.getMinecraft().resize(mc.displayWidth, mc.displayHeight);
        }

        if (showBlockUpdates) {

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor4f(1f, 0f, 0f, 0.2f);

            GL11.glDepthMask(false);

            //logger.log(Level.ALL, TransformUpdate.map.size());

            for (AxisAlignedBB bb : TransformUpdate.map.keySet()) {
                drawBox(bb, 0);
            }

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1f, 1f, 1f, 1f);

            GL11.glDepthMask(true);
        }

        if (showTileInfo) {

            GL11.glPushMatrix();

            Minecraft.getMinecraft().entityRenderer.setupOverlayRendering();

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glColor4f(1f, 1f, 1f, 1f);

            for (Map.Entry<Class, TransformTileEntityRendererDispatcher.AccumulatingRolling> e : TransformTileEntityRendererDispatcher.map.entrySet()) {
                e.getValue().finishSample();
            }

            AtomicInteger offset = new AtomicInteger();
            AtomicDouble totalTime = new AtomicDouble();
            TransformTileEntityRendererDispatcher.map.entrySet().stream().sorted((o1, o2) -> Double.compare(o2.getValue().getAverage(), o1.getValue().getAverage())).forEach(it -> {
                offset.getAndIncrement();

                double time = it.getValue().getAverage();

                double ms = time / 1000000d;

                totalTime.addAndGet(ms);

                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(it.getKey().getSimpleName() + ": " + String.format("%,.2f", ms) + "ms total", 200, 20 + offset.get() * 10, 16777215);
            });


            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("total render time: " + String.format("%,.2f", totalTime.get()), 200, 5, 16777215);


            TransformTileEntityRendererDispatcher.rendered.clear();

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            GL11.glPopMatrix();
        }

    }

    public static void drawBox(AxisAlignedBB boundingBox, float ticks) {
        if (boundingBox == null) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * (double) ticks;
        double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * (double) ticks;
        double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * (double) ticks;

        if (ticks == -1) {
            x = 0;
            y = 0;
            z = 0;
        }

        boundingBox = boundingBox.getOffsetBoundingBox(-x, -y, -z);

        // back
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();

        // left
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();

        // right
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glEnd();

        // front
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glEnd();

        // top
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();

        // bottom
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glEnd();
    }

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);

        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        Property disabledDimensionIds = config.get(Configuration.CATEGORY_GENERAL, "blacklistedLightDims", new int[]{-100});
        disabledDimensionIds.comment = "list of dimensions ids that are disabled for dynamic lights";
        blacklistedLightDims = disabledDimensionIds.getIntList();

        config.save();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);

        if (event.getSide() == Side.CLIENT) {
            DevCapes.getInstance().registerConfig("https://raw.githubusercontent.com/DrParadox7/LostEraCoreMod/master/capes/capes.json");
            if (FMLClientHandler.instance().hasOptifine()) {
                GameSettings settings = Minecraft.getMinecraft().gameSettings;
                try {
                    makeAccessible(GameSettings.class.getDeclaredField("ofSmoothFps")).set(settings, false);
                    makeAccessible(GameSettings.class.getDeclaredField("ofSmoothWorld")).set(settings, false);
                    makeAccessible(GameSettings.class.getDeclaredField("ofPreloadedChunks")).set(settings, 0);
                    makeAccessible(GameSettings.class.getDeclaredField("ofChunkUpdates")).set(settings, 1);
                    makeAccessible(GameSettings.class.getDeclaredField("ofChunkUpdatesDynamic")).set(settings, false);
                    makeAccessible(GameSettings.class.getDeclaredField("ofFastMath")).set(settings, false);
                    makeAccessible(GameSettings.class.getDeclaredField("ofLazyChunkLoading")).set(settings, false);
                    makeAccessible(GameSettings.class.getDeclaredField("ofFastRender")).set(settings, false);
                    makeAccessible(GameSettings.class.getDeclaredField("ofChunkLoading")).set(settings, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Field makeAccessible(Field in) {
        in.setAccessible(true);
        try {
            Field modifiersField = in.getClass().getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(in, in.getModifiers() & ~Modifier.FINAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

}
