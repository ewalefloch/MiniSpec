package generator.test;

import XMLIO.XMLAnalyser;
import generator.JavaGenerator;
import metaModel.minispec.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitialTest {

    // Note : J'ai ajouté &quot; autour de OVNI pour que le code Java généré soit valide ("OVNI")
    private final StringBuilder file = new StringBuilder(
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#2\" model=\"#1\" name=\"Satellite\" />\n" +
                    "    <Attribute id=\"#3\" entity=\"#2\" name=\"nom\" type=\"String\" default=\"&quot;OVNI&quot;\" />\n" +
                    "    <Attribute id=\"#4\" entity=\"#2\" name=\"numero\" type=\"Integer\" default=\"50\"/>\n" +
                    "    <Attribute id=\"#5\" entity=\"#2\" name=\"id\" type=\"Integer\" default=\"nextInt()\"/>\n" +
                    "</Root>");

    @Test
    void testSatelliteGenerationWithDefaults() {
        // 1. Analyse
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(file.toString());

        assertNotNull(model);
        assertEquals(1, model.getEntities().size());

        assertEquals("\"OVNI\"", model.getEntities().getFirst().getAttributes().get(0).getInitialValue());
        assertEquals("50", model.getEntities().getFirst().getAttributes().get(1).getInitialValue());

        // 2. Génération
        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();

        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");
    }
}