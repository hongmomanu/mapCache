package mapcatche.conmmon;
/*
 * 错误类
 * */
public class ErrorFunc {
	public static byte[] errorMapTitle(String filepath){
		return FileFunc.readFileByBytes(filepath);
		
	}

}
