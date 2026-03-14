package com.quiz.repository;

import com.quiz.exception.QuestionsNotFoundException;
import com.quiz.exception.QuizException;
import com.quiz.model.MultipleChoiceQuestion;
import com.quiz.model.Question;
import com.quiz.model.TrueFalseQuestion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads questions from the XML file using Java's DOM parser.
 *
 * DOM PARSER: reads entire XML into memory as a tree.
 * Perfect for small files like our questions list.
 *
 * We load from classpath (inside the JAR), not a filesystem path,
 * so the file is always bundled with the app.
 */
public class QuestionRepository {

    private static final Logger logger = LogManager.getLogger(QuestionRepository.class);
    private static final String QUESTIONS_FILE = "/data/questions.xml";

    /**
     * Loads ALL questions from XML.
     * Returns a List<Question> — could be MCQ or TF objects.
     * Caller doesn't need to know which type — polymorphism handles it.
     */
    public List<Question> loadAllQuestions() {
        List<Question> questions = new ArrayList<>();

        try {
            // Load file from classpath (works inside JAR too)
            InputStream stream = getClass().getResourceAsStream(QUESTIONS_FILE);
            if (stream == null) {
                throw new QuestionsNotFoundException("File not found: " + QUESTIONS_FILE);
            }

            // Parse XML into Document tree
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            doc.getDocumentElement().normalize();

            // Get all <question> elements
            NodeList nodeList = doc.getElementsByTagName("question");
            logger.debug("Found {} question elements in XML", nodeList.getLength());

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                String type = el.getAttribute("type"); // "MCQ" or "TF"

                Question question;

                if ("MCQ".equals(type)) {
                    question = parseMCQ(el);
                } else if ("TF".equals(type)) {
                    question = parseTrueFalse(el);
                } else {
                    logger.warn("Unknown question type '{}', skipping", type);
                    continue;
                }

                questions.add(question);
                logger.debug("Loaded: {}", question);
            }

            logger.info("Successfully loaded {} questions", questions.size());

        } catch (QuestionsNotFoundException e) {
            throw e; // re-throw our own exceptions as-is
        } catch (Exception e) {
            logger.error("Failed to load questions from XML", e);
            throw new QuizException("XML_PARSE_ERROR",
                    "Failed to parse questions file: " + e.getMessage(), e);
        }

        return questions;
    }

    // ── Private parsers ───────────────────────────────────

    private MultipleChoiceQuestion parseMCQ(Element el) {
        MultipleChoiceQuestion q = new MultipleChoiceQuestion();

        // Read attributes from <question type="MCQ" id="Q001" ...>
        q.setId(el.getAttribute("id"));
        q.setDifficulty(el.getAttribute("difficulty"));
        q.setCategory(el.getAttribute("category"));

        // Read child elements
        q.setText(getTagText(el, "text"));
        q.setCorrectAnswer(getTagText(el, "correctAnswer"));
        q.setExplanation(getTagText(el, "explanation"));
        q.setTimeLimit(parseInt(getTagText(el, "timeLimit"), 30));

        // Parse the <options> block
        // <options><option id="A">Text</option>...</options>
        Map<String, String> options = new LinkedHashMap<>();
        NodeList optionNodes = el.getElementsByTagName("option");
        for (int i = 0; i < optionNodes.getLength(); i++) {
            Element optEl = (Element) optionNodes.item(i);
            String optionId   = optEl.getAttribute("id");   // "A", "B", "C", "D"
            String optionText = optEl.getTextContent().trim();
            options.put(optionId, optionText);
        }
        q.setOptions(options);

        return q;
    }

    private TrueFalseQuestion parseTrueFalse(Element el) {
        TrueFalseQuestion q = new TrueFalseQuestion();

        q.setId(el.getAttribute("id"));
        q.setDifficulty(el.getAttribute("difficulty"));
        q.setCategory(el.getAttribute("category"));
        q.setText(getTagText(el, "text"));
        q.setCorrectAnswer(getTagText(el, "correctAnswer"));
        q.setExplanation(getTagText(el, "explanation"));
        q.setTimeLimit(parseInt(getTagText(el, "timeLimit"), 20));

        return q;
    }

    // ── Helper utilities ──────────────────────────────────

    /** Gets text content of first child element with given tag name */
    private String getTagText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent().trim();
    }

    /** Parses int safely, returns default if parsing fails */
    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}