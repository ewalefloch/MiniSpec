package generator.test;

import static org.junit.jupiter.api.Assertions.*;

import generator.JavaGenerator;
import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import metaModel.Entity;
import metaModel.Attribute;
import metaModel.type.Type;
import metaModel.type.SimpleType;
import metaModel.type.ResolvedReference;
import metaModel.type.collection.ListType;
import metaModel.type.collection.SetType;

public class CollectionGenTest {

    private final StringBuilder xmlContent = new StringBuilder(
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "\n" +
                    "    <Entity id=\"#10\" model=\"#1\" name=\"Flotte\" />\n" +
                    "    <Entity id=\"#20\" model=\"#1\" name=\"Satellite\" />\n" +
                    "\n" +
                    "    <Reference id=\"#RefFlotte\" entity=\"#10\" />\n" +
                    "    <Reference id=\"#RefFlotte\" name=\"Flotte\" />\n" +
                    "\n" +
                    "    <List id=\"#ListSat\" of=\"#20\" min=\"0\" max=\"10\" />\n" +
                    "\n" +
                    "    <Set id=\"#SetInt\" of=\"Integer\" />\n" +
                    "\n" +
                    "    <Array id=\"#ArrayStation\" of=\"#RefStation\" size=\"10\" />\n" +
                    "\n" +
                    "\n" +
                    "    <Attribute id=\"#A1\" entity=\"#10\" name=\"mesSatellites\" type=\"#ListSat\" />\n" +
                    "    <Attribute id=\"#A2\" entity=\"#10\" name=\"nom\" type=\"String\" />\n" +
                    "\n" +
                    "    <Attribute id=\"#A3\" entity=\"#20\" name=\"codes\" type=\"#SetInt\" />\n" +
                    "    <Attribute id=\"#A4\" entity=\"#20\" name=\"cibles\" type=\"#ArrayStation\" />\n" +
                    "    <Attribute id=\"#A5\" entity=\"#20\" name=\"altitude\" type=\"Integer\" />\n" +
                    "    <Attribute id=\"#A6\" entity=\"#20\" name=\"maFlotte\" type=\"#RefFlotte\" />\n" +
                    "\n" +
                    "</Root>");

    @Test
    void testCollectionsAndTypes() {
        // 1. Parsing
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(xmlContent.toString());
        assertNotNull(model);

        Entity flotte = findEntity(model, "Flotte");
        Entity satellite = findEntity(model, "Satellite");

        // --- VERIFICATIONS METAMODELE ---

        //SimpleType (String)
        Attribute attNom = findAttribute(flotte, "nom");
        assertInstanceOf(SimpleType.class, attNom.getType(), "Le type 'String' doit être un SimpleType");
        assertEquals("String", attNom.getType().getName());

        // List<ResolvedReference>
        Attribute attSatellites = findAttribute(flotte, "mesSatellites");
        assertInstanceOf(ListType.class, attSatellites.getType());
        Type baseTypeSat = ((ListType) attSatellites.getType()).getElementType();

        assertInstanceOf(ResolvedReference.class, baseTypeSat);
        assertEquals("Satellite", ((ResolvedReference) baseTypeSat).getRefEnt().getName());

        //Set<SimpleType>
        Attribute attCodes = findAttribute(satellite, "codes");
        assertInstanceOf(SetType.class, attCodes.getType());
        Type baseTypeInt = ((SetType) attCodes.getType()).getElementType();
        assertInstanceOf(SimpleType.class, baseTypeInt);
        assertEquals("Integer", baseTypeInt.getName());

        // --- VERIFICATION GENERATION CODE ---

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String code = generator.getCode();

        System.out.println("=== CODE GÉNÉRÉ (Collections & Types) ===");
        System.out.println(code);
        System.out.println("=========================================");
    }

    private Entity findEntity(Model m, String name) {
        return m.getEntities().stream()
                .filter(e -> name.equals(e.getName())).findFirst()
                .orElseThrow(() -> new AssertionError("Entité " + name + " introuvable"));
    }

    private Attribute findAttribute(Entity e, String name) {
        return e.getAttributes().stream()
                .filter(a -> name.equals(a.getName())).findFirst()
                .orElseThrow(() -> new AssertionError("Attribut " + name + " introuvable"));
    }
}