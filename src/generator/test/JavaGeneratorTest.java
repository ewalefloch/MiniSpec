package generator.test;

import static org.junit.jupiter.api.Assertions.*;

import generator.JavaGenerator;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.minispec.Model;

class JavaGeneratorTest {

    private final StringBuilder file = new StringBuilder("<Root model=\"#1\">\n" +
            "    <Model id=\"#1\" />\n" +
            "    <Entity id=\"#2\" model=\"#1\" name=\"Satellite\" />\n" +
            "    <Attribute id=\"#3\" entity=\"#2\" name=\"nom\" type=\"String\" />\n" +
            "    <Attribute id=\"#4\" entity=\"#2\" name=\"id\" type=\"Integer\" />\n" +
            "</Root>");


    @Test
    void testSatelliteGeneration() {

        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(file.toString());

        assertNotNull(model);
        assertEquals(1, model.getEntities().size(),"Une entité doit être présente dans le modèle");
        assertEquals("Satellite", model.getEntities().getFirst().getName(), "Le nom de l'entité doit être 'Satellite'");
        assertEquals(2, model.getEntities().getFirst().getAttributes().size(), "L'entité 'Satellite' doit avoir deux attributs");
        assertEquals("nom", model.getEntities().getFirst().getAttributes().getFirst().getName(), "Le premier attribut doit être 'nom'");
        assertEquals("id", model.getEntities().getFirst().getAttributes().getLast().getName(), "Le second attribut doit être 'id'");

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();

        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");
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