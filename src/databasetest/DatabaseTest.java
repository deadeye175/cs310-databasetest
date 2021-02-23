package databasetest;

import java.sql.*;
import org.json.simple.*;
import java.util.LinkedHashMap;

public class DatabaseTest{
    public static void main(String[] args){
        JSONArray jsonarray = getJSONData();
        System.out.println(jsonarray);
    }
    public static JSONArray getJSONData(){
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;
        JSONArray databasearray = new JSONArray();
        String query;
        boolean hasresults;
        int resultCount, columnCount;
        try{
            String server = ("jdbc:mysql://localhost/p2_test");
            String username = "root";
            String password = "cs488";
            System.out.println("Connecting to " + server + "...");
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
            if(conn.isValid(0)){   
                System.out.println("Connected Successfully!"); 
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                System.out.println("Submitting Query ...");
                hasresults = pstSelect.execute();
                System.out.println("Getting Results ...");
                while(hasresults || pstSelect.getUpdateCount() != -1) {
                    if(hasresults){
                        resultset = pstSelect.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        while(resultset.next()){
                            LinkedHashMap<String, String> addtoarray = new LinkedHashMap<>();
                            for (int i = 2; i <= columnCount; i++) {      
                                addtoarray.put(metadata.getColumnLabel(i), resultset.getString(i));
                            }
                            databasearray.add(addtoarray);
                        }  
                    }
                    else{
                        resultCount = pstSelect.getUpdateCount();
                        if(resultCount == -1){
                            break;
                        }
                    }
                    hasresults = pstSelect.getMoreResults();
                }
            }        
            conn.close();   
        }
        catch(Exception e){
            System.err.println(e.toString());
        }
        finally{
            if (resultset != null) { try { resultset.close(); resultset = null;} catch (Exception e) {} }
            if (pstSelect != null) { try { pstSelect.close(); pstSelect = null;} catch (Exception e) {} }
            if (pstUpdate != null) { try { pstUpdate.close(); pstUpdate = null;} catch (Exception e) {} }  
        }
        return databasearray;  
    }
}