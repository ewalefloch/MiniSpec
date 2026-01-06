package generator.test;

import XMLIO.XMLAnalyser;
import generator.JavaGenerator;
import metaModel.minispec.Attribute;
import metaModel.minispec.Entity;
import metaModel.minispec.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InitialTest {

    private final String complexXml =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#Sat\" model=\"#1\" name=\"Satellite\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <Attribute id=\"#A1\" entity=\"#Sat\" name=\"nom\" type=\"String\" default=\"&quot;Sputnik&quot;\" />\n" +
                    "\n" +
                    "    \n" +
                    "    \n" +
                    "    <Attribute id=\"#A2\" entity=\"#Sat\" name=\"position\" type=\"Point\" default=\"new Point(0,0)\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <Attribute id=\"#A3\" entity=\"#Sat\" name=\"id\" type=\"Integer\" default=\"nextId()\"/>\n" +
                    "\n" +
                    "    \n" +
                    "    \n" +
                    "    <Attribute id=\"#A4\" entity=\"#Sat\" name=\"cle\" type=\"String\" default=\"generateKey()\"/>\n" +
                    "</Root>";

    private final String complexXmlDouble =
            "<Root model=\"#1\">\n" +
                    "    <Model id=\"#1\" />\n" +
                    "    <Entity id=\"#Sat\" model=\"#1\" name=\"Satellite\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <Attribute id=\"#A1\" entity=\"#Sat\" name=\"nom\" type=\"String\" default=\"&quot;Sputnik&quot;\" />\n" +
                    "\n" +
                    "    \n" +
                    "    \n" +
                    "    <Attribute id=\"#A2\" entity=\"#Sat\" name=\"position\" type=\"Point\" default=\"new Point(0,0)\" />\n" +
                    "\n" +
                    "    \n" +
                    "    <Attribute id=\"#A3\" entity=\"#Sat\" name=\"id\" type=\"Integer\" default=\"nextId()\"/>\n" +
                    "\n" +
                    "    \n" +
                    "    \n" +
                    "    <Attribute id=\"#A4\" entity=\"#Sat\" name=\"cle\" type=\"String\" default=\"nextId()\"/>\n" +
                    "</Root>";

    @Test
    void testComplexInitializationModelAbstract() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(complexXml);

        assertNotNull(model);
        Entity sat = model.getEntities().getFirst();

        Attribute attrPosition = findAttribute(sat, "position");
        assert attrPosition != null;
        assertEquals("new Point(0,0)", attrPosition.getInitialValue(),
                "Le modèle doit stocker le constructeur 'new Point(0,0)'");

        Attribute attrId = findAttribute(sat, "id");
        assert attrId != null;
        assertEquals("nextId()", attrId.getInitialValue(),
                "Le modèle doit stocker l'appel de fonction 'nextId()'");

        Attribute attrKey = findAttribute(sat, "cle");
        assert attrKey != null;
        assertEquals("generateKey()", attrKey.getInitialValue(),
                "Le modèle doit stocker l'appel de fonction 'generateKey()'");

        JavaGenerator generator = new JavaGenerator();
        generator.setAbstract(true);
        model.accept(generator);
        String generatedCode = generator.getCode();
        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");
    }

    @Test
    void testComplexInitializationModel() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(complexXml);

        assertNotNull(model);
        Entity sat = model.getEntities().getFirst();

        Attribute attrPosition = findAttribute(sat, "position");
        assert attrPosition != null;
        assertEquals("new Point(0,0)", attrPosition.getInitialValue(),
                "Le modèle doit stocker le constructeur 'new Point(0,0)' tel quel");

        Attribute attrId = findAttribute(sat, "id");
        assert attrId != null;
        assertEquals("nextId()", attrId.getInitialValue(),
                "Le modèle doit stocker l'appel de fonction 'nextId()'");

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();
        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");
    }

    @Test
    void testComplexInitializationModelDouble() {
        XMLAnalyser analyser = new XMLAnalyser();
        Model model = analyser.getModelFromString(complexXml);

        assertNotNull(model);
        Entity sat = model.getEntities().getFirst();

        Attribute attrPosition = findAttribute(sat, "position");
        assert attrPosition != null;
        assertEquals("new Point(0,0)", attrPosition.getInitialValue(),
                "Le modèle doit stocker le constructeur 'new Point(0,0)' tel quel");

        Attribute attrId = findAttribute(sat, "id");
        assert attrId != null;
        assertEquals("nextId()", attrId.getInitialValue(),
                "Le modèle doit stocker l'appel de fonction 'nextId()'");

        assertEquals(1, nombreAttribute(sat, "nextId()"),
                "Le modèle doit stocker une fois la fonction 'nextId()'");

        JavaGenerator generator = new JavaGenerator();
        model.accept(generator);
        String generatedCode = generator.getCode();
        System.out.println("--- Code Généré (Test Satellite) ---");
        System.out.println(generatedCode);
        System.out.println("------------------------------------");
    }

    private Attribute findAttribute(Entity e, String name) {
        for (Attribute a : e.getAttributes()) {
            if (a.getName().equals(name)) return a;
        }
        return null;
    }

    private int nombreAttribute(Entity e, String initalValue) {
        int count = 0;
        for (Attribute a : e.getAttributes()) {
            if (a.getInitialValue().equals(initalValue)) {
                count++;
            }
        }
        return count;
    }

}