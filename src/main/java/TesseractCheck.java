/**
 * using Tess4j Java wrapper for Tesseract OCR
 * @author Vijeth P H
 */

import java.io.File;
import net.sourceforge.tess4j.*;

public class TesseractCheck {
    public static void main(String[] args) {
        // System.setProperty("jna.library.path", "32".equals(System.getProperty("sun.arch.data.model")) ? "lib/win32-x86" : "lib/win32-x86-64");

        File imageFile = new File("test.png");
        Tesseract instance = new Tesseract();  // JNA Interface Mapping
         //ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        // File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build bundles English data
        // instance.setDatapath(tessDataFolder.getPath());
        instance.setDatapath("/usr/local/share/tessdata");
        instance.setTessVariable("user_defined_dpi","300");
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}