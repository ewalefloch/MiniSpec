package generator.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import metaModel.ModelValidator; // Assurez-vous d'avoir créé cette classe
import generator.JavaGenerator;

class HeritageTest {

    private final String xmlValid =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <Entity id=\"#30\" model=\"#1\" name=\"Machine\" />\n" +
                    "    <Entity id=\"#10\" model=\"#1\" name=\"Flotte\" />\n" +
                    "    <Entity id=\"#20\" model=\"#1\" name=\"Satellite\" extends=\"#30\"/>\n" +
                    "\n" +
                    "    \n" +
                    "    <Reference id=\"#RefFlotte\" entity=\"#10\" />\n" +
                    "    \n" +
                    "    <Reference id=\"#RefStation\" name=\"StationSol\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <List id=\"#ListSat\" of=\"#20\" min=\"0\" max=\"10\" />\n" +
                    "    <Set id=\"#SetInt\" of=\"Integer\" />\n" +
                    "    <Array id=\"#ArrayStation\" of=\"#RefStation\" size=\"10\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <Attribute id=\"#A1\" entity=\"#10\" name=\"mesSatellites\" type=\"#ListSat\" />\n" +
                    "    <Attribute id=\"#A2\" entity=\"#10\" name=\"nom\" type=\"String\" />\n" +
                    "\n" +
                    "    <Attribute id=\"#A3\" entity=\"#20\" name=\"codes\" type=\"#SetInt\" />\n" +
                    "    <Attribute id=\"#A4\" entity=\"#20\" name=\"cibles\" type=\"#ArrayStation\" />\n" +
                    "    <Attribute id=\"#A5\" entity=\"#20\" name=\"altitude\" type=\"Integer\" />\n" +
                    "    <Attribute id=\"#A6\" entity=\"#20\" name=\"maFlotte\" type=\"#RefFlotte\" />\n" +
                    "\n" +
                    "</Root>";

    // Cas Erreur : Circularité (A extends B, B extends A)
    private final String xmlCircle =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#A\" model=\"#1\" name=\"EntiteA\" extends=\"#B\" />\n" +
                    "    <Entity id=\"#B\" model=\"#1\" name=\"EntiteB\" extends=\"#A\" />\n" +
                    "</Root>";

    @Test
    void testValidInheritanceAndCollections() throws Exception {
        // 1. Parsing
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlValid);
        assertNotNull(model);

        // 2. Validation (Doit passer sans erreur)
        new ModelValidator().validate(model);

        // 3. Génération
        JavaGenerator gen = new JavaGenerator();
        model.accept(gen);
        String code = gen.getCode();

        System.out.println("=== CODE GÉNÉRÉ (HeritageTest) ===");
        System.out.println(code);
        System.out.println("==================================");

        // --- VERIFICATIONS ---

        // A. Vérification de l'héritage
        // "public class Satellite extends Machine"
        assertTrue(code.contains("public class Satellite extends Machine"),
                "La classe Satellite doit hériter de Machine");

        // B. Vérification des Collections générées

        // 1. List (mesSatellites -> List<Satellite>)
        assertTrue(code.contains("import java.util.List;"), "Manque l'import List");
        assertTrue(code.contains("public List<Satellite> mesSatellites;"),
                "Le type List<Satellite> est incorrect");

        // 2. Set (codes -> Set<Integer>)
        assertTrue(code.contains("import java.util.Set;"), "Manque l'import Set");
        assertTrue(code.contains("public Set<Integer> codes;"),
                "Le type Set<Integer> est incorrect");

        // 3. Array (cibles -> StationSol[])
        // Note: StationSol vient d'une UnresolvedReference, donc on vérifie le nom
        assertTrue(code.contains("public StationSol[] cibles;"),
                "Le type tableau StationSol[] est incorrect");

        // 4. Référence (maFlotte -> Flotte)
        assertTrue(code.contains("public Flotte maFlotte;"),
                "Le type référence Flotte est incorrect");
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