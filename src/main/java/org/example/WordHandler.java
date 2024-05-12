package org.example;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordHandler {

    public static void changeDocx(LinkedHashMap<String, Object> change, String pathOriginal, String templateDoc) {
        String tempPath = ".tempdocument.docx";
        try {
            Path dirOrigem = Paths.get(pathOriginal + templateDoc);
            Path dirDestino = Paths.get(tempPath);
            Files.copy(dirOrigem, dirDestino, StandardCopyOption.REPLACE_EXISTING);
            KeyFinder kf = new KeyFinder("<", ">");
            try (XWPFDocument doc = new XWPFDocument(OPCPackage.open(tempPath))) {

                headerChange(doc);

                bodyChange(doc, change, kf);

                tableChange(change, doc.getTableArray(0), kf);

                doc.write(new FileOutputStream("changed_" + templateDoc));
            }
        } catch (Exception e) {

        } finally {
            try {
                File tempFile = new File(tempPath);
                tempFile.delete();
            } catch (Exception e) {

            }
        }
    }

    public static void headerChange(XWPFDocument doc) {
        Map<String, File> change = new LinkedHashMap<>();
        change.put("mbh_logo", new File("src/main/resources/mbh_logo.png"));
        KeyFinder kf = new KeyFinder("\\[", "\\]");
        for (XWPFHeader h : doc.getHeaderList()) {
            for (XWPFParagraph p : h.getParagraphs()) {
                paragraphChange(p, change, kf);
            }
        }
    }

    private static void bodyChange(XWPFDocument doc, Map<String, Object> change, KeyFinder kf) {
        for (XWPFParagraph p : doc.getParagraphs()) {
            paragraphChange(p, change, kf);
        }
    }

    private static void tableChange(LinkedHashMap<String, Object> change, XWPFTable table, KeyFinder kf) {

        int rowcount = -1;
        ArrayList<LinkedHashMap<String, String>> changelist = (ArrayList<LinkedHashMap<String, String>>) change.get("row-data");
        for (XWPFTableRow row : table.getRows()) {
            if (rowcount >= 0) {
                LinkedHashMap<String, String> changemap = changelist.get(rowcount);
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        paragraphChange(p, changemap, kf);
                    }
                }
            }
            rowcount++;
        }

    }


    private static <T> void paragraphChange(XWPFParagraph p, Map<String, T> change, KeyFinder kf) {
        ArrayList<String> keysInDocx = kf.match(p.getText(), change);
        ArrayList<Integer> keysStartPosInDocx = kf.startPosFind(p.getText(), keysInDocx);
        ArrayList<Integer> keysEndPosInDocx = kf.endPosFind(p.getText(), keysInDocx);
        ArrayList<Integer> deleteRunList = new ArrayList<>();
        int aktPos = 0;
        int aktKey = 0;
        int runcount = 0;

        for (XWPFRun r : p.getRuns()) {
            String text = r.getText(0);

            if (text != null) {
                int tempAktPos = aktPos + text.length();

                selectDeleteRunList(text, keysStartPosInDocx, keysEndPosInDocx, deleteRunList,
                        aktKey, runcount, aktPos, r);

                if (insertTextToRun(text, change, keysStartPosInDocx, keysInDocx, keysEndPosInDocx,
                        aktKey, aktPos, tempAktPos, r)) aktKey++;

                aktPos = tempAktPos;

            }
            runcount++;
        }

        for (int i = deleteRunList.size(); i > 0; i--) {
            p.removeRun(deleteRunList.get(i - 1));
        }
    }

    private static <T> boolean insertTextToRun(String text, Map<String, T> change,
                                               ArrayList<Integer> keysStartPosInDocx,
                                               ArrayList<String> keysInDocx,
                                               ArrayList<Integer> keysEndPosInDocx,
                                               int aktKey, int aktPos, int tempAktPos,
                                               XWPFRun r) {
        boolean result = false;
        try {
            if (keysStartPosInDocx.get(aktKey) >= aktPos &&
                    keysStartPosInDocx.get(aktKey) < aktPos + text.length()) {

                r.setText(text.substring(0, keysStartPosInDocx.get(aktKey) - aktPos), 0);
                runSetData(r, change.get(keysInDocx.get(aktKey)));
                if (tempAktPos > keysEndPosInDocx.get(aktKey)) {
                    r.setText(text.substring(keysEndPosInDocx.get(aktKey) - aktPos, tempAktPos - aktPos));
                }
                result = true;
            }
        } catch (Exception e) {
            // IndexOutOfBoundsException in ArrayList
        }
        return result;
    }

    private static void selectDeleteRunList(String text,
                                            ArrayList<Integer> keysStartPosInDocx,
                                            ArrayList<Integer> keysEndPosInDocx,
                                            ArrayList<Integer> deleteRunList,
                                            int aktKey, int runcount, int aktPos,
                                            XWPFRun r) {
        try {
            if (keysStartPosInDocx.get(aktKey - 1) < aktPos) {
                if (keysEndPosInDocx.get(aktKey - 1) >= aktPos + text.length()) {
                    deleteRunList.add(runcount);
                } else {
                    r.setText(text.substring(keysEndPosInDocx.get(aktKey) - aktPos), 0);
                }
            }
        } catch (Exception e) {
            // IndexOutOfBoundsException in ArrayList
        }
    }

    private static <T> void runSetData(XWPFRun r, T t) throws IOException, InvalidFormatException {
        if (t instanceof File) {
            int[] pictureSize = askPictureSize((File) t);
            double ratio = pictureSize[0] / 50.0;

            String imgFile = ((File) t).getAbsolutePath();
            FileInputStream is = new FileInputStream(imgFile);
            r.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG,
                    imgFile, Units.toEMU((int) Math.round(pictureSize[0] / ratio)),
                    Units.toEMU((int) Math.round(pictureSize[1] / ratio)));

            is.close();
        } else if (t instanceof String) {
            r.setText((String) t);
        }

    }

    private static int[] askPictureSize(File file) throws IOException {
        int[] result = new int[2];
        BufferedImage image = ImageIO.read(file);
        result[0] = image.getWidth();
        result[1] = image.getHeight();
        return result;
    }


}
