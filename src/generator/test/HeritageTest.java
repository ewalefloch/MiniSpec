package generator.test;

import static org.junit.jupiter.api.Assertions.*;

import metaModel.ModelValidator;
import org.junit.jupiter.api.Test;
import XMLIO.XMLAnalyser;
import metaModel.Model;
import generator.JavaGenerator;

class HeritageTest {

    // Cas Valide : Meuble <- Chaise
    private final String xmlValid =
            "<Root model=\"#1\">" +
                    "  <Model id=\"#1\" />" +
                    "  <Entity id=\"#10\" model=\"#1\" name=\"Meuble\" />" +
                    "  <Entity id=\"#20\" model=\"#1\" name=\"Chaise\" extends=\"#10\" />" + // Chaise extends Meuble
                    "</Root>";

    // Cas Erreur : Circularité (A extends B, B extends A)
    private final String xmlCircle =
            "<Root model=\"#1\">" +
                    "  <Model id=\"#1\" />" +
                    "  <Entity id=\"#A\" model=\"#1\" name=\"A\" extends=\"#B\" />" +
                    "  <Entity id=\"#B\" model=\"#1\" name=\"B\" extends=\"#A\" />" +
                    "</Root>";

    // Cas Erreur : Duplication Attribut
    private final String xmlDuplicate =
            "<Root model=\"#1\">" +
                    "  <Model id=\"#1\" />" +
                    "  <Entity id=\"#10\" model=\"#1\" name=\"Parent\" />" +
                    "  <Attribute id=\"#A1\" entity=\"#10\" name=\"poids\" type=\"Integer\" />" +

                    "  <Entity id=\"#20\" model=\"#1\" name=\"Enfant\" extends=\"#10\" />" +
                    "  <Attribute id=\"#A2\" entity=\"#20\" name=\"poids\" type=\"Integer\" />" + // Erreur !
                    "</Root>";

    @Test
    void testValidInheritance() throws Exception {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlValid);

        // 1. Validation
        new ModelValidator().validate(model);

        // 2. Génération
        JavaGenerator gen = new JavaGenerator();
        model.accept(gen);
        String code = gen.getCode();

        System.out.println(code);
        assertTrue(code.contains("public class Chaise extends Meuble"));
    }

    @Test
    void testCircularityError() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlCircle);

        Exception exception = assertThrows(Exception.class, () -> {
            new ModelValidator().validate(model);
        });

        System.out.println("Erreur capturée : " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Circularité"));
    }

    @Test
    void testDuplicateAttributeError() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlDuplicate);

        Exception exception = assertThrows(Exception.class, () -> {
            new ModelValidator().validate(model);
        });

        System.out.println("Erreur capturée : " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Attribut dupliqué"));
    }
}
