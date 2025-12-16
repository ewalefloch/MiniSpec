package generator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.Model;

class JavaGeneratorTest {

    private generation.FileGenerator fileGenerator = new generation.FileGenerator();

    /**
     * Teste la génération d'une seule entité simple (Satellite)
     * Correspond à l'exemple de la Figure 5 du PDF.
     */
    @Test
    void testSatelliteGeneration() {

        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromFilenamed("./exemple/Exemple3.xml");

        assertNotNull(model);
        assertEquals(1, model.getEntities().size());
        assertEquals("Satellite", model.getEntities().getFirst().getName());
        assertEquals(2, model.getEntities().getFirst().getAttributes().size());

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();

        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");

        assertTrue(generatedCode.contains("public class Satellite"), "La classe Satellite doit être définie");
        assertTrue(generatedCode.contains("String nom;"), "L'attribut nom doit être généré");
        assertTrue(generatedCode.contains("Integer id;"), "L'attribut id doit être généré");
        assertTrue(generatedCode.contains("Satellite() { }"), "Le constructeur vide doit être généré");

        fileGenerator.generateFile("Satellite", generatedCode, "./output");
    }

    @Test
    void testEmptyModel() {
        String xmlContent = "<Root model=\"#1\"><Model id=\"#1\" /></Root>";

        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlContent);

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);

        String code = generator.getCode();
        assertTrue(code.trim().isEmpty(), "Le code généré pour un modèle vide doit être vide");
    }
}