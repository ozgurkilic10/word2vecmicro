package tr.edu.mu.ceng.ir.word2vecmicro.server;

import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.VectorsConfiguration;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Word2VecBean {

    protected Logger logger = LoggerFactory.getLogger(Word2VecServer.class.getName());
    private Word2Vec vec;

    public Word2VecBean(){
        vec = loadWord2Vec();
    }

    public Word2Vec getWord2Vec(){
        return vec;
    }

    private Word2Vec loadWord2Vec() {
        Word2Vec vec = null;
        String targetFileFolder = "vectors";
        logger.debug("=====Starting Reading Vectors  from " + targetFileFolder + " =====");
        try {
            vec = readUnCompressed(targetFileFolder);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error reading target folder " + targetFileFolder, e);
        }
        logger.debug("=====Reading Vectors Completed=====");

        return vec;
    }


    private Word2Vec readUnCompressed(String targetFileFolder) throws IOException {
        Word2Vec vec = null;
        logger.debug("Trying full model restoration...");


        File fileSyn0 = new File(targetFileFolder + "/syn0.txt");
        File fileSyn1 = new File(targetFileFolder + "/syn1.txt");
        File fileCodes = new File(targetFileFolder + "/codes.txt");
        File fileHuffman = new File(targetFileFolder + "/huffman.txt");
        File fileFreq = new File(targetFileFolder + "/frequencies.txt");


        int originalFreq = Nd4j.getMemoryManager().getOccasionalGcFrequency();
        boolean originalPeriodic = Nd4j.getMemoryManager().isPeriodicGcActive();

        if (originalPeriodic)
            Nd4j.getMemoryManager().togglePeriodicGc(false);

        Nd4j.getMemoryManager().setOccasionalGcFrequency(50000);

        try {
            InputStream stream = new FileInputStream(targetFileFolder + "/config.json");
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }

            VectorsConfiguration configuration = VectorsConfiguration.fromJson(builder.toString().trim());

            // we read first 4 files as w2v model
            vec = WordVectorSerializer.readWord2VecFromText(fileSyn0, fileSyn1, fileCodes, fileHuffman, configuration);

            if (fileFreq.exists()) {
                // we read frequencies from frequencies.txt, however it's possible that we might not have this file
                stream = new FileInputStream(fileFreq);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] split = line.split(" ");
                        VocabWord word = vec.getVocab().tokenFor(WordVectorSerializer.decodeB64(split[0]));
                        word.setElementFrequency((long) Double.parseDouble(split[1]));
                        word.setSequencesCount((long) Double.parseDouble(split[2]));
                    }
                }

            }
            if (fileSyn1.exists()) {
                stream = new FileInputStream(fileSyn1);

                try (InputStreamReader isr = new InputStreamReader(stream);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String line = null;
                    List<INDArray> rows = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        String[] split = line.split(" ");
                        double array[] = new double[split.length];
                        for (int i = 0; i < split.length; i++) {
                            array[i] = Double.parseDouble(split[i]);
                        }
                        rows.add(Nd4j.create(array));
                    }

                    // it's possible to have full model without syn1Neg
                    if (!rows.isEmpty()) {
                        INDArray syn1Neg = Nd4j.vstack(rows);
                        ((InMemoryLookupTable) vec.getLookupTable()).setSyn1Neg(syn1Neg);
                    }
                }

            }

        } finally {
            if (originalPeriodic)
                Nd4j.getMemoryManager().togglePeriodicGc(true);
            Nd4j.getMemoryManager().setOccasionalGcFrequency(originalFreq);
        }
        return vec;
    }
}
