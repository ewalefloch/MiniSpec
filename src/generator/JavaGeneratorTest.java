package generator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.Model;

class JavaGeneratorTest {

    /**
     * Teste la génération d'une seule entité simple (Satellite)
     * Correspond à l'exemple de la Figure 5 du PDF.
     */
    @Test
    void testSatelliteGeneration() {
        // 1. Simulation du XML (Source)
        String xmlContent =
                "<Root model=\"#1\">" +
                        "  <Model id=\"#1\" />" +
                        "  <Entity model=\"#1\" id=\"#20\" name=\"Satellite\">" +
                        "    <Attribute name=\"nom\" type=\"String\"/>" +
                        "    <Attribute name=\"id\" type=\"Integer\"/>" +
                        "  </Entity>" +
                        "</Root>";

        // 2. Analyse (XML -> Objets)
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlContent);

        // Vérification préliminaire que le modèle est bien chargé
        assertNotNull(model);
        assertEquals(1, model.getEntities().size());
        assertEquals("Satellite", model.getEntities().get(0).getName());
        assertEquals(2, model.getEntities().get(0).getAttributes().size());

        // 3. Génération (Objets -> Code Java)
        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();

        // 4. Affichage pour debug (optionnel)
        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");

        // 5. Assertions sur le code généré
        assertTrue(generatedCode.contains("public class Satellite"), "La classe Satellite doit être définie");
        assertTrue(generatedCode.contains("String nom;"), "L'attribut nom doit être généré");
        assertTrue(generatedCode.contains("Integer id;"), "L'attribut id doit être généré");
        assertTrue(generatedCode.contains("Satellite() { }"), "Le constructeur vide doit être généré");
    }

    /**
     * Teste la génération avec deux entités.
     */
    @Test
    void testMultipleEntities() {
        String xmlContent =
                "<Root model=\"#1\">" +
                        "  <Model id=\"#1\" />" +
                        "  <Entity model=\"#1\" id=\"#2\" name=\"A\">" +
                        "     <Attribute name=\"attA\" type=\"String\"/>" +
                        "  </Entity>" +
                        "  <Entity model=\"#1\" id=\"#3\" name=\"B\">" +
                        "     <Attribute name=\"attB\" type=\"Integer\"/>" +
                        "  </Entity>" +
                        "</Root>";

        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlContent);

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String code = generator.getCode();

        assertTrue(code.contains("public class A"));
        assertTrue(code.contains("public class B"));
        assertTrue(code.contains("public String attA;"));
        assertTrue(code.contains("public Integer attB;"));
    }

    /**
     * Teste un modèle vide (pas d'entités).
     */
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