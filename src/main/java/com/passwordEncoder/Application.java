package com.passwordEncoder;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Arrays;

import static java.lang.Character.toUpperCase;

public class Application {
    public static void main(String[] args) {
        // Initialize global variables
        Set<Character> firstHalfCharacters = new
                HashSet<>(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', '0', '1', '2', '3', '4'));
        Sheet sheet = null;
        Workbook workbook = null;

        // initialize the file selector dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Find and select your 'Password Encoder.xlsx' file.");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        File file = null;
        boolean validFile = false;

        while(!validFile) {
            // create a variable for the selected file
            fileChooser.showSaveDialog(null);
            file = fileChooser.getSelectedFile();

            // check if they exited the file selector without grabbing a file
            if(file == null) {
                JOptionPane.showMessageDialog(null, "Maybe next time. Goodbye!");
                System.exit(0);
            }
            // validate that they selected the correct file.
            else if (!file.getName().equals("Password Encoder.xlsx")) {
                JOptionPane.showMessageDialog(null, "Your file must be named 'Password Encoder.xlsx' (Excel file extension).\n\nOr else.....");
                fileChooser.setSelectedFile(null);
            } else {
                validFile = true;
            }
        }

        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        String propertiesFilePath = System.getProperty("user.dir") + "\\src\\main\\java\\com\\passwordEncoder\\app.properties";

        // Locate and open the spreadsheet to read from
        try (FileInputStream fileInputStream = new FileInputStream(new
                File(filePath));
        ) {
            workbook = new XSSFWorkbook(fileInputStream);
            sheet = workbook.getSheetAt(0);
            Properties properties = new Properties();
            properties.load(new FileInputStream(propertiesFilePath));

            String stringOne = properties.getProperty("stringOne");
            String stringTwo = properties.getProperty("stringTwo");
            String stringThree = properties.getProperty("stringThree");
            String stringFour = properties.getProperty("stringFour");
            String stringFive = properties.getProperty("stringFive");
            String stringBlock = properties.getProperty("stringBlock");
            int characterPosition = Integer.parseInt(properties.getProperty("characterPosition"));
            char backupChar = properties.getProperty("backupCharacter").charAt(0);

            // Encode password for each company/service
            for (Row row : sheet) {
                Cell cell = row.getCell(0);

                // Perform empty row validation
                if(cell == null || cell.getCellType() == CellType.BLANK) {
                    continue;
                }

                String cellValue = cell.getStringCellValue();
                char firstChar = cellValue.length() >= characterPosition ? toUpperCase(cellValue.charAt(characterPosition - 1)) : backupChar;
                StringBuilder password = new StringBuilder();

                password.append(firstHalfCharacters.contains(firstChar) ? stringOne : stringTwo);
                password.append(stringBlock);
                password.append(firstHalfCharacters.contains(firstChar) ? stringThree : stringFour);
                password.append(firstChar + stringFive);

                row.createCell(1).setCellValue(password.toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Your app.properties file must be filled in before using this application");
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // Open an output stream to the spreadsheet
        try (FileOutputStream fileOutputStream = new
                FileOutputStream(filePath);
        ){
            // Write in the passwords
            workbook.write(fileOutputStream);
            workbook.close();
            JOptionPane.showMessageDialog(null, "Your passwords have been created!\n♪♪ ┏(-_-)┛ ♪ ┗(^o^)┓ ♪ ┏(-_-)┛ ♪♪");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() +
                    "\n\nMake sure that you save and close the spreadsheet before running this program.");
        }
    }
}
