package generator.test;

import static org.junit.jupiter.api.Assertions.*;

import generator.JavaGenerator;
import metaModel.minispec.Entity;
import org.junit.jupiter.api.Test;
import XMLIO.XMLAnalyser;
import metaModel.minispec.Model;

class AssociationTest {

    private final StringBuilder file = new StringBuilder("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
            "<Root model=\"#1\">\n" +
            "    <Model id=\"#1\" />\n" +
            "\n" +
            "    <Entity id=\"#10\" model=\"#1\" name=\"Flotte\" />\n" +
            "\n" +
            "    <Entity id=\"#20\" model=\"#1\" name=\"Satellite\" />\n" +
            "\n" +
            "    <Attribute id=\"#21\" entity=\"#20\" name=\"nom\" type=\"String\" />\n" +
            "\n" +
            "    <Attribute id=\"#22\" entity=\"#20\" name=\"parent\" type=\"Flotte\" />\n" +
            "\n" +
            "    <Attribute id=\"#11\" entity=\"#10\" name=\"nom\" type=\"String\" />\n" +
            "\n" +
            "    <Attribute id=\"#12\" entity=\"#10\" name=\"numero\" type=\"Integer\" />\n" +
            "</Root>");

    @Test
    void testAssociationSimple() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(file.toString());

        assertNotNull(model);
        assertEquals(2, model.getEntities().size());

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();

        System.out.println("--- Code Généré (Association Simple) ---");
        System.out.println(generatedCode);
        System.out.println("----------------------------------------");

    }

}