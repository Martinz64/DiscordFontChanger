package dev.martinz.discordfontchanger;

import android.content.res.AssetManager;

import androidx.annotation.Nullable;

import java.io.FileInputStream;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);

        if(lpparam.packageName.startsWith("com.discord")){
            XposedHelpers.findAndHookConstructor(
                    "com.discord.fonts.DiscordFont",
                    lpparam.classLoader,
                    String.class,
                    int.class,
                    String.class,
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String fontTypeName = (String)param.args[0];
                            String fontSrc = (String)param.args[2];


                            XposedBridge.log("--------");
                            XposedBridge.log("Font: " + param.args[0]);
                            XposedBridge.log("Font: " + param.args[1]);
                            XposedBridge.log("Font: " + param.args[2]);
                            XposedBridge.log("--------");
                            //param.args[2] = "Rubik-Normal, NotoSans-Normal";
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                        }
                    });
            XposedHelpers.findAndHookMethod(AssetManager.class, "open", String.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String orig_path = (String) param.args[0];
                    XposedBridge.log("Asset: " + param.args[0]);
                    if(!orig_path.startsWith("fonts")) return;
                    if(getFontReplacement(orig_path) == null) return;

                    String fontReplacementPath = getFontReplacement(orig_path);

                    //param.setResult(new FileInputStream("/data/data/com.discord/files/fonts/Rubik-Regular.ttf"));
                    XposedBridge.log("Replaced " + param.args[0] + " with " + fontReplacementPath);
                    param.setResult(new FileInputStream(fontReplacementPath));
                }
            });
        }
    }

    @Nullable
    String getFontReplacement(String name){
        String base = "/data/data/com.discord/files/fonts";
        // SourceCodePro: SourceCodePro-Semibold
        // PrimaryNormal: ggsans-Normal
        // PrimaryNormalItalic: ggsans-NormalItalic
        // PrimaryMedium: ggsans-Medium
        // PrimarySemibold: ggsans-Semibold
        // PrimaryBold: ggsans-Bold
        // PrimaryExtraBold: ggsans-ExtraBold
        if(name.startsWith("fonts/ggsans-Normal")){
            return base + "/regular.ttf";
        }
        if(name.startsWith("fonts/ggsans-NormalItalic")){
            return base + "/italic.ttf";
        }
        if(name.startsWith("fonts/ggsans-Medium")){
            return base + "/medium.ttf";
        }
        if(name.startsWith("fonts/ggsans-Semibold")){
            return base + "/semibold.ttf";
        }
        if(name.startsWith("fonts/ggsans-Bold")){
            return base + "/bold.ttf";
        }
        if(name.startsWith("fonts/ggsans-ExtraBold")){
            return base + "/extrabold.ttf";
        }

        return null;
    }

}
