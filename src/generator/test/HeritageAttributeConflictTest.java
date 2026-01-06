package generator.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.minispec.Model;
import metaModel.minispec.modelValidator.ModelValidatorAttributeUniqueHeritage;

class HeritageAttributeConflictTest {

    private final String xmlSuccess =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#P\" model=\"#1\" name=\"Mere\" />\n" +
                    "    <Attribute id=\"#A1\" entity=\"#P\" name=\"poids\" type=\"Integer\" />\n" +
                    "\n" +
                    "    <Entity id=\"#C\" model=\"#1\" name=\"Fille\" extends=\"#P\" />\n" +
                    "    <Attribute id=\"#A2\" entity=\"#C\" name=\"taille\" type=\"Integer\" />\n" +
                    "</Root>";

    private final String xmlDirectParentConflict =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#P\" model=\"#1\" name=\"Mere\" />\n" +
                    "    <Attribute id=\"#A1\" entity=\"#P\" name=\"id\" type=\"String\" />\n" +
                    "\n" +
                    "    <Entity id=\"#C\" model=\"#1\" name=\"Fille\" extends=\"#P\" />\n" +
                    "    <Attribute id=\"#A2\" entity=\"#C\" name=\"id\" type=\"Integer\" />\n" +
                    "</Root>";

    private final String xmlDistantParentConflict =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#GP\" model=\"#1\" name=\"GrandMere\" />\n" +
                    "    <Attribute id=\"#A1\" entity=\"#GP\" name=\"statut\" type=\"Boolean\" />\n" +
                    "\n" +
                    "    <Entity id=\"#P\" model=\"#1\" name=\"Mere\" extends=\"#GP\" />\n" +
                    "\n" +
                    "    <Entity id=\"#C\" model=\"#1\" name=\"Fille\" extends=\"#P\" />\n" +
                    "    <Attribute id=\"#A2\" entity=\"#C\" name=\"statut\" type=\"Integer\" />\n" +
                    "</Root>";

    @Test
    void testNoConflict() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlSuccess);

        assertDoesNotThrow(() -> {
            new ModelValidatorAttributeUniqueHeritage().validate(model);
        });
        System.out.println("Succès : Aucun conflit détecté.");
    }

    @Test
    void testDirectParentConflict() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlDirectParentConflict);

        Exception exception = assertThrows(Exception.class, () -> {
            new ModelValidatorAttributeUniqueHeritage().validate(model);
        },"Une exception doit être lancée en cas de conflit d'héritage direct.");

        System.out.println("Exception capturée : " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Conflit d'héritage"),"Le message doit indiquer un Conflit d'héritage");
        assertTrue(exception.getMessage().contains("id"),"Le message doit mentionner l'attribut 'id'");
    }

    @Test
    void testDistantParentConflict() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlDistantParentConflict);

        Exception exception = assertThrows(Exception.class, () -> {
            new ModelValidatorAttributeUniqueHeritage().validate(model);
        },"Une exception doit être lancée en cas de conflit d'héritage distant.");

        System.out.println("Exception capturée : " + exception.getMessage());
        assertTrue(exception.getMessage().contains("GrandMere"),"Le message doit mentionner l'entité 'GrandMere'");
        assertTrue(exception.getMessage().contains("statut"),"Le message doit indiquer l'attribut 'statut'");
    }
}