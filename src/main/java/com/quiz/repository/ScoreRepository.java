package com.quiz.repository;

import com.quiz.exception.QuizException;
import com.quiz.model.QuizScore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads quiz scores from scores.xml.
 * This file lives on the filesystem (not classpath) so scores persist
 * between sessions and can grow as players play more games.
 */
public class ScoreRepository {

    private static final Logger logger = LogManager.getLogger(ScoreRepository.class);
    private final String filePath;

    public ScoreRepository(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    /** Creates scores.xml if it doesn't exist yet */
    private void ensureFileExists() {
        File file = new File(filePath);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs(); // create parent dirs
        }
        if (!file.exists()) {
            try (FileWriter fw = new FileWriter(file)) {
                fw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<scores>\n</scores>");
                logger.info("Created scores file: {}", filePath);
            } catch (IOException e) {
                throw new QuizException("FILE_ERROR", "Cannot create scores file");
            }
        }
    }

    /** Saves a new score entry to the XML file */
    public void saveScore(QuizScore score) {
        List<QuizScore> all = loadAllScores();
        all.add(score);
        writeAll(all);
        logger.info("Saved score: {}", score);
    }

    /** Loads all scores from XML */
    public List<QuizScore> loadAllScores() {
        List<QuizScore> scores = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) return scores;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nodes = doc.getElementsByTagName("score");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                QuizScore s = new QuizScore();
                s.setPlayerName(getTag(el, "playerName"));
                s.setScore(Integer.parseInt(getTag(el, "score")));
                s.setTotalQuestions(Integer.parseInt(getTag(el, "totalQuestions")));
                s.setTimeTakenSeconds(Long.parseLong(getTag(el, "timeTaken")));
                s.setPlayedAt(LocalDateTime.parse(getTag(el, "playedAt")));
                scores.add(s);
            }
            logger.debug("Loaded {} scores", scores.size());

        } catch (Exception e) {
            logger.error("Error loading scores", e);
        }
        return scores;
    }

    /** Writes all scores to the XML file */
    private void writeAll(List<QuizScore> scores) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("scores");
            doc.appendChild(root);

            for (QuizScore s : scores) {
                Element el = doc.createElement("score");
                addChild(doc, el, "playerName",    s.getPlayerName());
                addChild(doc, el, "score",         String.valueOf(s.getScore()));
                addChild(doc, el, "totalQuestions",String.valueOf(s.getTotalQuestions()));
                addChild(doc, el, "timeTaken",     String.valueOf(s.getTimeTakenSeconds()));
                addChild(doc, el, "playedAt",      s.getPlayedAt().toString());
                root.appendChild(el);
            }

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            tf.transform(new DOMSource(doc), new StreamResult(new File(filePath)));

        } catch (Exception e) {
            logger.error("Error saving scores", e);
            throw new QuizException("SAVE_ERROR", "Failed to save scores");
        }
    }

    private String getTag(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent().trim() : "";
    }

    private void addChild(Document doc, Element parent, String tag, String value) {
        Element child = doc.createElement(tag);
        child.setTextContent(value == null ? "" : value);
        parent.appendChild(child);
    }
}