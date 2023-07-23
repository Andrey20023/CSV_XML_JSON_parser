import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileNameXML = "data.xml";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        jsonWriter(json, "data.json");
        List<Employee> listXml = parseXML(fileNameXML);
        String json2 = listToJson(listXml);
        jsonWriter(json2, "data2.json");
    }
    public static List<Employee> parseCSV(String[] arr, String filename) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        System.out.println(json);
        return json;
    }

    public static void jsonWriter(String json, String filename) {
        try (
                FileWriter file = new
                        FileWriter(filename)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Employee> parseXML(String xmlFilename) throws ParserConfigurationException, IOException, SAXException {
        // NamedNodeMap asd = new NamedNodeMap<>;
        HashMap<String, String> attributes = new HashMap<>();
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFilename));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                NodeList nodeListEmpl = node.getChildNodes();
                for (int j = 0; j < nodeListEmpl.getLength(); j++) {
                    Node node_ = nodeListEmpl.item(j);
                    if (Node.ELEMENT_NODE == node_.getNodeType()) {
                        attributes.put(node_.getNodeName(), node_.getTextContent());
                    }
                }

                list.add(new Employee(
                        Long.parseLong(attributes.get("id")),
                        attributes.get("firstName"),
                        attributes.get("lastName"),
                        attributes.get("country"),
                        Integer.parseInt(attributes.get("age"))));
                attributes.clear();
            }
        }
        return list;
    }
}





