package XMLIO.config;

import metaModel.javaConfig.JavaConfig;
import metaModel.javaConfig.ModelConfig;
import metaModel.javaConfig.PrimitiveConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class AnalyzerConfig {

    public JavaConfig load(String xmlContent) {
        JavaConfig config = new JavaConfig();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            Element root = doc.getDocumentElement();

            NodeList models = root.getElementsByTagName("model");
            for (int i = 0; i < models.getLength(); i++) {
                Element el = (Element) models.item(i);
                String name = el.getAttribute("name");
                String pkg = el.getAttribute("package");

                config.addModel(new ModelConfig(name, pkg));
            }

            NodeList prims = root.getElementsByTagName("primitive");
            for (int i = 0; i < prims.getLength(); i++) {
                Element el = (Element) prims.item(i);
                String name = el.getAttribute("name");
                String type = el.getAttribute("type");
                String pkg  = el.getAttribute("package");

                config.addPrimitive(new PrimitiveConfig(name, type, pkg));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }
}