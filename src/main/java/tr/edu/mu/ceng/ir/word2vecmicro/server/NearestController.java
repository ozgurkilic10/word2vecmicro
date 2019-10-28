package tr.edu.mu.ceng.ir.word2vecmicro.server;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class NearestController {

    @Autowired Word2VecBean w2v;

    protected Logger logger = LoggerFactory.getLogger(NearestController.class
            .getName());

    @RequestMapping("/nearest")
    public String nearestWords(@RequestParam(defaultValue="0") String word,
                        @RequestParam(defaultValue="0") String count) {

        logger.info("Finding nearest " + count + " nearest words for " + word);

        Collection<String> nearestWords = w2v.getWord2Vec().wordsNearest(word,Integer.parseInt(count));
        String result = new Gson().toJson(nearestWords);

        return result;
    }
}
