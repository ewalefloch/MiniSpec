package XMLIO;

import javax.xml.parsers.*;

import metaModel.visiteur.MinispecElement;
import org.w3c.dom.*;
import org.xml.sax.*;


import metaModel.*;
import metaModel.Entity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XMLAnalyser {

	// Les clés des 2 Map sont les id 
	
	// Map des instances de la syntaxe abstraite (metamodel)
	protected Map<String, MinispecElement> minispecIndex;
	// Map des elements XML
	protected Map<String, Element> xmlElementIndex;

	public XMLAnalyser() {
		this.minispecIndex = new HashMap<String, MinispecElement>();
		this.xmlElementIndex = new HashMap<String, Element>();
	}

	protected Model modelFromElement(Element e) {
		return new Model();
	}

	protected Entity entityFromElement(Element e) {
		String name = e.getAttribute("name");
		Entity entity = new Entity();
		entity.setName(name);

		if (e.hasAttribute("model")) {
			String modelId = e.getAttribute("model");
			if (this.xmlElementIndex.containsKey(modelId)) {
				MinispecElement parent = minispecElementFromXmlElement(this.xmlElementIndex.get(modelId));
				if (parent instanceof Model) {
					((Model) parent).addEntity(entity);
				}
			}
		}
		return entity;
	}

	protected Attribute attributeFromElement(Element e) {
		Attribute attr = new Attribute();
		attr.setName(e.getAttribute("name"));
		attr.setType(e.getAttribute("type"));

		if (e.hasAttribute("entity")) {
			String entityId = e.getAttribute("entity");

			if (this.xmlElementIndex.containsKey(entityId)) {
				MinispecElement parent = minispecElementFromXmlElement(this.xmlElementIndex.get(entityId));
				if (parent instanceof Entity) {
					((Entity) parent).addAttribute(attr);
				}
			}
		}
		return attr;
	}

	protected MinispecElement minispecElementFromXmlElement(Element e) {
		String id = e.getAttribute("id");
		MinispecElement result = this.minispecIndex.get(id);
		if (result != null) return result;
		String tag = e.getTagName();
        result = switch (tag) {
            case "Model" -> modelFromElement(e);
            case "Entity" -> entityFromElement(e);
            case "Attribute" -> attributeFromElement(e);
            default -> result;
        };
		this.minispecIndex.put(id, result);
		return result;
	}

	// alimentation du map des elements XML
	protected void firstRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				Element child = (Element) n;
				String id = child.getAttribute("id");
				this.xmlElementIndex.put(id, child);
			}
		}
	}

	// alimentation du map des instances de la syntaxe abstraite (metamodel)
	protected void secondRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element) {
				minispecElementFromXmlElement((Element)n);
			}
		}
	}

	public Model getModelFromDocument(Document document) {
		Element e = document.getDocumentElement();
		
		firstRound(e);
		
		secondRound(e);
		
		Model model = (Model) this.minispecIndex.get(e.getAttribute("model"));
				
		return model;
	}
	
	public Model getModelFromInputStream(InputStream stream) {
		try {
			// création d'une fabrique de documents
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

			// création d'un constructeur de documents
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			Document document = constructeur.parse(stream);
			return getModelFromDocument(document);

		} catch (ParserConfigurationException pce) {
			System.out.println("Erreur de configuration du parseur DOM");
			System.out.println("lors de l'appel à fabrique.newDocumentBuilder();");
		} catch (SAXException se) {
			System.out.println("Erreur lors du parsing du document");
			System.out.println("lors de l'appel à construteur.parse(xml)");
		} catch (IOException ioe) {
			System.out.println("Erreur d'entrée/sortie");
			System.out.println("lors de l'appel à construteur.parse(xml)");
		}
		return null;
	}
	
	public Model getModelFromString(String contents) {		
		InputStream stream = new ByteArrayInputStream(contents.getBytes());
		return getModelFromInputStream(stream);
	}
	
	public Model getModelFromFile(File file) {		
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getModelFromInputStream(stream);
	}

	public Model getModelFromFilenamed(String filename) {
			File file = new File(filename);
			return getModelFromFile(file);
	}
}
