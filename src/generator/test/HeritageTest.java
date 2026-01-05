package generator.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import XMLIO.config.AnalyzerConfig; // NOUVEAU
import metaModel.javaConfig.JavaConfig; // NOUVEAU
import metaModel.minispec.Model;
import metaModel.minispec.ModelValidator;
import generator.JavaGenerator;

class HeritageTest {

    // XML Valide (Minispec)
    private final String xmlValid =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "\n" +
                    "    <Entity id=\"#30\" model=\"#1\" name=\"Machine\" />\n" +
                    "    <Entity id=\"#10\" model=\"#1\" name=\"Flotte\" />\n" +
                    "    <Entity id=\"#20\" model=\"#1\" name=\"Satellite\" extends=\"#30\"/>\n" +
                    "\n" +
                    "    <Reference id=\"#RefFlotte\" entity=\"#10\" />\n" +
                    "    <Reference id=\"#RefStation\" name=\"StationSol\" />\n" +
                    "\n" +
                    "    <List id=\"#ListSat\" of=\"#20\" min=\"0\" max=\"10\" />\n" +
                    "    <Set id=\"#SetInt\" of=\"Integer\" />\n" +
                    "    <Array id=\"#ArrayStation\" of=\"#RefStation\" size=\"10\" />\n" +
                    "\n" +
                    "    <Attribute id=\"#A1\" entity=\"#10\" name=\"mesSatellites\" type=\"#ListSat\" />\n" +
                    "    <Attribute id=\"#A2\" entity=\"#10\" name=\"nom\" type=\"String\" />\n" +
                    "\n" +
                    "    <Attribute id=\"#A3\" entity=\"#20\" name=\"codes\" type=\"#SetInt\" />\n" +
                    "    <Attribute id=\"#A4\" entity=\"#20\" name=\"cibles\" type=\"#ArrayStation\" />\n" +
                    "    <Attribute id=\"#A5\" entity=\"#20\" name=\"altitude\" type=\"Integer\" />\n" +
                    "    <Attribute id=\"#A6\" entity=\"#20\" name=\"maFlotte\" type=\"#RefFlotte\" />\n" +
                    "\n" +
                    "</Root>";

    private final String xmlConfig =
            "<java-code>\n" +
                    "    <model name=\"Machine\" package=\"m2tiil.space\"/>\n" +
                    "    <model name=\"Flotte\" package=\"m2tiil.space\"/>\n" +
                    "    <model name=\"Satellite\" package=\"m2tiil.space\"/>\n" +
                    "    <primitive name=\"String\" type=\"String\"/>\n" +
                    "    <primitive name=\"Integer\" type=\"Integer\"/>\n" +
                    "    <primitive name=\"List\" type=\"ArrayList\" package=\"java.util.ArrayList\"/>\n" +
                    "    <primitive name=\"Set\" type=\"HashSet\" package=\"java.util.HashSet\"/>\n" +
                    "    <primitive name=\"Array\" type=\"Array\" package=\"m2tiil.Array\"/>\n" +
                    "</java-code>";

    //Erreur
    private final String xmlCircle =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#A\" model=\"#1\" name=\"EntiteA\" extends=\"#B\" />\n" +
                    "    <Entity id=\"#B\" model=\"#1\" name=\"EntiteB\" extends=\"#A\" />\n" +
                    "</Root>";

    @Test
    void testValidInheritanceAndCollections() throws Exception {
        // 1. Chargement de la Configuration (NOUVEAU)
        AnalyzerConfig configLoader = new AnalyzerConfig();
        JavaConfig javaConfig = configLoader.load(xmlConfig);
        assertNotNull(javaConfig, "La configuration Java doit être chargée");

        // 2. Parsing du Modèle Minispec
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlValid);
        assertNotNull(model);

        // 3. Validation
        new ModelValidator().validate(model);

        // 4. Génération
        JavaGenerator gen = new JavaGenerator(javaConfig);
        model.accept(gen);
        String code = gen.getCode();

        System.out.println("=== CODE GÉNÉRÉ (HeritageTest) ===");
        System.out.println(code);
        System.out.println("==================================");

    }

    @Test
    void testCircularityError() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlCircle);

        Exception exception = assertThrows(Exception.class, () -> {
            new ModelValidator().validate(model);
        });

        System.out.println("Erreur Circularité capturée : " + exception.getMessage());
    }
}