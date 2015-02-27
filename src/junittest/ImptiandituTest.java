package junittest;

import java.util.HashMap;
import java.util.Map;
import mapcatche.business.impl.Imptianditu;
import org.junit.Before;
import org.junit.Test;

public class ImptiandituTest {

	private static Imptianditu imptianditu = new Imptianditu();
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMaptodb() {
            
             Map<String, Object> params = new HashMap<String, Object>();
         params.put("layerlevel", 13);
          params.put("maplayer", 2);
        params.put("maxx", 120.5);
        params.put("minx", 120);
        params.put("maxy", 30.4);
        params.put("miny", 30.1);

        //test.maptodb(params);
        
       imptianditu. makeDownTiles(params);
		
		//fail("Not yet implemented");
	}

}
