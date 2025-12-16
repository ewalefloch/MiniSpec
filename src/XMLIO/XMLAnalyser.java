package XMLIO;

import javax.xml.parsers.*;

import metaModel.visiteur.MinispecElement;
import org.w3c.dom.*;

import metaModel.*;
import metaModel.Entity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import metaModel.type.*;
import metaModel.type.collection.*;

public class XMLAnalyser {

	protected Map<String, MinispecElement> minispecIndex;
	protected Map<String, Element> xmlElementIndex;

	public XMLAnalyser() {
		this.minispecIndex = new HashMap<>();
		this.xmlElementIndex = new HashMap<>();
	}

	/**
	 * Méthode intelligente pour résoudre un type à partir d'une chaîne (ID ou Nom).
	 */
	private Type resolveType(String typeStr) {
		if (typeStr == null || typeStr.isEmpty()) return null;

		if (this.xmlElementIndex.containsKey(typeStr)) {
			MinispecElement elem = minispecElementFromXmlElement(this.xmlElementIndex.get(typeStr));

			if (elem instanceof Type) {
				return (Type) elem;
			} else if (elem instanceof Entity) {
				return new ResolvedReference((Entity) elem);
			}
		}


		if (isBaseType(typeStr)) {
			return new SimpleType(typeStr);
		}

		return new UnresolvedReference(typeStr);
	}

	private boolean isBaseType(String name) {
		return "String".equalsIgnoreCase(name) || "Integer".equalsIgnoreCase(name) ||
				"Boolean".equalsIgnoreCase(name) || "Float".equalsIgnoreCase(name) ||
				"Double".equalsIgnoreCase(name);
	}

	protected Model modelFromElement(Element e) {
		return new Model();
	}

	protected Entity entityFromElement(Element e) {
		String name = e.getAttribute("name");
		Entity entity = new Entity();
		entity.setName(name);

		if (e.hasAttribute("model")) {
			MinispecElement elem = minispecElementFromXmlElement(this.xmlElementIndex.get(e.getAttribute("model")));
			if (elem instanceof Model) {
				((Model) elem).addEntity(entity);
			}
		}
		return entity;
	}

	protected Attribute attributeFromElement(Element e) {
		String name = e.getAttribute("name");
		String typeStr = e.getAttribute("type");

		Entity entity = (Entity) minispecElementFromXmlElement(this.xmlElementIndex.get(e.getAttribute("entity")));

		Attribute attribute = new Attribute();
		attribute.setName(name);

		Type type = resolveType(typeStr);
		attribute.setType(type);

		if (entity != null) {
			entity.addAttribute(attribute);
		}
		return attribute;
	}

	protected SimpleType simpleTypeFromElement(Element e) {
		String name = e.getAttribute("name");
		return new SimpleType(name);
	}

	private ReferenceType referenceFromElement(Element e) {
		if (e.hasAttribute("entity")) {
			String entityId = e.getAttribute("entity");
			MinispecElement elem = minispecElementFromXmlElement(this.xmlElementIndex.get(entityId));
			if (elem instanceof Entity) {
				return new ResolvedReference((Entity) elem);
			}
		}
		String targetName = e.getAttribute("name");
		return new UnresolvedReference(targetName);
	}

	private Type collectionFromElement(Element e) {
		String name = e.getAttribute("name"); // "Array", "List", "Set"...

		String typeStr = e.getAttribute("type");
		if (typeStr == null || typeStr.isEmpty()) typeStr = e.getAttribute("of");

		Type baseType = resolveType(typeStr);

		Integer min = parseInteger(e.getAttribute("min"));
		Integer max = parseInteger(e.getAttribute("max"));
		Integer size = parseInteger(e.getAttribute("size"));

		if ("Array".equalsIgnoreCase(name)) {
			return new ArrayType(size != null ? size : 0, baseType);
		}
		else if ("Set".equalsIgnoreCase(name)) {
			return new SetType(min, max, baseType);
		}
		else if ("Bag".equalsIgnoreCase(name)) {
			return new BagType(min, max, baseType);
		}
		else {
			return new ListType(min, max, baseType);
		}
	}

	protected MinispecElement minispecElementFromXmlElement(Element e) {
		String id = e.getAttribute("id");
		if (minispecIndex.containsKey(id)) return minispecIndex.get(id);

		String tag = e.getTagName();
		MinispecElement result = switch (tag) {
			case "Model" -> modelFromElement(e);
			case "Entity" -> entityFromElement(e);
			case "Attribute" -> attributeFromElement(e);

			case "Type", "SimpleType" -> simpleTypeFromElement(e);

			case "Collection" -> collectionFromElement(e);
			case "Reference" -> referenceFromElement(e);
			default -> null;
		};

		if (result != null && !id.isEmpty()) {
			this.minispecIndex.put(id, result);
		}
		return result;
	}

	// --- Utilitaires de Parsing ---

	private Integer parseInteger(String val){
		if (val == null || val.isEmpty()) return null;
		try { return Integer.parseInt(val); } catch (NumberFormatException e) { return null; }
	}

	protected void firstRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node n = nodes.item(i);
			if (n instanceof Element child) {
                String id = child.getAttribute("id");
				if (!id.isEmpty()) {
					this.xmlElementIndex.put(id, child);
				}
			}
		}
	}

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
		return (Model) this.minispecIndex.get(e.getAttribute("model"));
	}

	public Model getModelFromInputStream(InputStream stream) {
		try {
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			Document document = constructeur.parse(stream);
			return getModelFromDocument(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Model getModelFromString(String contents) {
		InputStream stream = new ByteArrayInputStream(contents.getBytes());
		return getModelFromInputStream(stream);
	}

	public Model getModelFromFile(File file) {
		try {
			return getModelFromInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Model getModelFromFilenamed(String filename) {
		return getModelFromFile(new File(filename));
	}
}