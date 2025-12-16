package XMLIO;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import metaModel.*;
import metaModel.Entity;
import metaModel.type.*;
import metaModel.type.collection.*;
import metaModel.visiteur.MinispecElement;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XMLAnalyser {

	protected Map<String, MinispecElement> minispecIndex;
	protected Map<String, Element> xmlElementIndex;

	public XMLAnalyser() {
		this.minispecIndex = new HashMap<>();
		this.xmlElementIndex = new HashMap<>();
	}

	// --- RESOLUTION DES TYPES ---
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

		return new SimpleType(typeStr);
	}

	// --- PARSING DES ELEMENTS ---

	protected Model modelFromElement(Element e) {
		return new Model();
	}

	protected Entity entityFromElement(Element e) {
		Entity entity = new Entity();
		entity.setName(e.getAttribute("name"));

		if (e.hasAttribute("extends")) {
			String superId = e.getAttribute("extends");
			Element superEl = this.xmlElementIndex.get(superId);
			if(superEl != null) {
				MinispecElement superObj = minispecElementFromXmlElement(superEl);
				if(superObj instanceof Entity) {
					entity.setSuperEntity((Entity) superObj);
				}
			}
		}

		if (e.hasAttribute("model")) {
			MinispecElement model = minispecElementFromXmlElement(this.xmlElementIndex.get(e.getAttribute("model")));
			if (model instanceof Model) ((Model) model).addEntity(entity);
		}
		return entity;
	}

	protected Attribute attributeFromElement(Element e) {
		Attribute attribute = new Attribute();
		attribute.setName(e.getAttribute("name"));

		// Résolution du type via ID ou Nom
		attribute.setType(resolveType(e.getAttribute("type")));

		String entityId = e.getAttribute("entity");
		MinispecElement parent = minispecElementFromXmlElement(this.xmlElementIndex.get(entityId));
		if (parent instanceof Entity) {
			((Entity) parent).addAttribute(attribute);
		}
		return attribute;
	}

	private ReferenceType referenceFromElement(Element e) {
		String id = e.getAttribute("id");
		if (e.hasAttribute("entity")) {
			String refId = e.getAttribute("entity");
			MinispecElement target = minispecElementFromXmlElement(this.xmlElementIndex.get(refId));
			if(target instanceof Entity) return new ResolvedReference((Entity)target);
		}
		return new UnresolvedReference(e.getAttribute("name"));
	}

	// --- GESTION DES COLLECTIONS  ---
	private ArrayType arrayTypeFromElement(Element e) {
		String typeStr = e.getAttribute("of");
		if (typeStr.isEmpty()) typeStr = e.getAttribute("type");
		Type baseType = resolveType(typeStr);

		Integer size = parseInteger(e.getAttribute("size"));

		return new ArrayType(size != null ? size : 0, baseType);
	}

	private ListType listTypeFromElement(Element e) {
		String typeStr = e.getAttribute("of");
		if (typeStr.isEmpty()) typeStr = e.getAttribute("type");
		Type baseType = resolveType(typeStr);

		Integer min = parseInteger(e.getAttribute("min"));
		Integer max = parseInteger(e.getAttribute("max"));

		return new ListType(min, max, baseType);
	}

	private SetType setTypeFromElement(Element e) {
		String typeStr = e.getAttribute("of");
		if (typeStr.isEmpty()) typeStr = e.getAttribute("type");
		Type baseType = resolveType(typeStr);

		Integer min = parseInteger(e.getAttribute("min"));
		Integer max = parseInteger(e.getAttribute("max"));

		return new SetType(min, max, baseType);
	}

	private BagType bagTypeFromElement(Element e) {
		String typeStr = e.getAttribute("of");
		if (typeStr.isEmpty()) typeStr = e.getAttribute("type");
		Type baseType = resolveType(typeStr);

		Integer min = parseInteger(e.getAttribute("min"));
		Integer max = parseInteger(e.getAttribute("max"));

		return new BagType(min, max, baseType);
	}

	// --- DISPATCH PRINCIPAL ---
	protected MinispecElement minispecElementFromXmlElement(Element e) {
		String id = e.getAttribute("id");
		if (minispecIndex.containsKey(id)) return minispecIndex.get(id);

		String tag = e.getTagName();
		MinispecElement result = switch (tag) {
			case "Model" -> modelFromElement(e);
			case "Entity" -> entityFromElement(e);
			case "Attribute" -> attributeFromElement(e);

			case "List" -> listTypeFromElement(e);
			case "Set" -> setTypeFromElement(e);
			case "Bag" -> bagTypeFromElement(e);
			case "Array" -> arrayTypeFromElement(e);

			case "Reference" -> referenceFromElement(e);

			default -> null;
		};

		if (result != null && id != null && !id.isEmpty()) {
			this.minispecIndex.put(id, result);
		}
		return result;
	}

	// --- UTILITAIRES ---
	private Integer parseInteger(String val) {
		try { return Integer.parseInt(val); } catch (Exception e) { return null; }
	}

	protected void firstRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element child) {
				if (child.hasAttribute("id")) this.xmlElementIndex.put(child.getAttribute("id"), child);
			}
		}
	}

	protected void secondRound(Element el) {
		NodeList nodes = el.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element child) minispecElementFromXmlElement(child);
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
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

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