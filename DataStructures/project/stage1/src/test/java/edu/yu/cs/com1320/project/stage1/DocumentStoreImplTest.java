package edu.yu.cs.com1320.project.stage1;

import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
public class DocumentStoreImplTest {
    DocumentStoreImpl ds;
    @BeforeEach
    void beforeEach(){
        ds = new DocumentStoreImpl();
    }

}
