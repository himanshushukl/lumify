package io.lumify.analystsNotebook.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.lumify.core.model.ontology.Concept;
import io.lumify.core.model.ontology.OntologyRepository;
import io.lumify.core.model.properties.LumifyProperties;
import org.apache.commons.codec.binary.Base64;
import org.securegraph.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityType {
    private static final String ONTOLOGY_CONCEPT_METADATA_ICON_FILE_KEY = "http://lumify.io/analystsNotebook#iconFile";
    public static final String ICON_FILE_DEFAULT = "Default";

    @JacksonXmlProperty(isAttribute = true)
    private String name;

    @JacksonXmlProperty(isAttribute = true)
    private String iconFile;

    public EntityType() {

    }

    public EntityType(String name, String iconFile) {
        this.name = name;
        this.iconFile = iconFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconFile() {
        return iconFile;
    }

    public void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }

    public static List<EntityType> createForVertices(Iterable<Vertex> vertices, OntologyRepository ontologyRepository) {
        Map<String, String> conceptTypeIconFileMap = new HashMap<String, String>();
        for (Vertex vertex : vertices) {
            String conceptType = LumifyProperties.CONCEPT_TYPE.getPropertyValue(vertex);
            if (!conceptTypeIconFileMap.containsKey(conceptType)) {
                Concept concept = ontologyRepository.getConceptByIRI(conceptType);
                String iconFile = getMetadataIconFile(concept, ontologyRepository);
                if (iconFile == null) {
                    iconFile = EntityType.ICON_FILE_DEFAULT;
                }
                conceptTypeIconFileMap.put(conceptType, iconFile);
            }
        }

        List<EntityType> entityTypes = new ArrayList<EntityType>();
        for (Map.Entry<String, String> entry : conceptTypeIconFileMap.entrySet()) {
            entityTypes.add(new EntityType(entry.getKey(), entry.getValue()));
        }
        return entityTypes;
    }

    private static String getMetadataIconFile(Concept concept, OntologyRepository ontologyRepository) {
        Map<String, String> metadata = concept.getMetadata();
        if (metadata.containsKey(ONTOLOGY_CONCEPT_METADATA_ICON_FILE_KEY)) {
            return metadata.get(ONTOLOGY_CONCEPT_METADATA_ICON_FILE_KEY);
        } else {
            concept = ontologyRepository.getParentConcept(concept);
            if (concept != null) {
                return getMetadataIconFile(concept, ontologyRepository);
            } else {
                return null;
            }
        }
    }
}
