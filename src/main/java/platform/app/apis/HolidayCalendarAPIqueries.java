package platform.app.apis;

import com.labplanet.servicios.app.GlobalAPIsParams;
import static platform.app.apis.IncidentAPIactions.MANDATORY_PARAMS_MAIN_SERVLET;
import databases.TblsApp;
import databases.features.Token;
import functionaljavaa.holidayscalendar.HolidaysCalendarEnums.CalendarAPIqueriesEndpoints;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lbplanet.utilities.LPArray;
import lbplanet.utilities.LPDate;
import lbplanet.utilities.LPFrontEnd;
import lbplanet.utilities.LPHttp;
import lbplanet.utilities.LPJson;
import lbplanet.utilities.LPPlatform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import trazit.enums.EnumIntTableFields;
import static trazit.enums.EnumIntTableFields.getAllFieldNames;
import trazit.queries.QueryUtilitiesEnums;
import trazit.session.ProcedureRequestSession;

/**
 *
 * @author User
 */
public class HolidayCalendarAPIqueries extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("deprecation")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request=LPHttp.requestPreparation(request);
        response=LPHttp.responsePreparation(response);

        String language = LPFrontEnd.setLanguage(request); 
        String actionName = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_ACTION_NAME);
        String finalToken = request.getParameter(GlobalAPIsParams.REQUEST_PARAM_FINAL_TOKEN);                   
        CalendarAPIqueriesEndpoints endPoint = null;
        try{
            endPoint = CalendarAPIqueriesEndpoints.valueOf(actionName.toUpperCase());
        }catch(Exception e){
            LPFrontEnd.servletReturnResponseError(request, response, LPPlatform.ApiErrorTraping.PROPERTY_ENDPOINT_NOT_FOUND.getErrorCode(), new Object[]{actionName, this.getServletName()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());              
            return;                   
        }
        ProcedureRequestSession procReqInstance = ProcedureRequestSession.getInstanceForQueries(request, response, endPoint, false, true);

        Object[] areMandatoryParamsInResponse = LPHttp.areMandatoryParamsInApiRequest(request, MANDATORY_PARAMS_MAIN_SERVLET.split("\\|"));                       
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(areMandatoryParamsInResponse[0].toString())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, 
                LPPlatform.ApiErrorTraping.MANDATORY_PARAMS_MISSING.getErrorCode(), new Object[]{areMandatoryParamsInResponse[1].toString()}, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;          
        }             
        
        Token token = new Token(finalToken);
        if (LPPlatform.LAB_FALSE.equalsIgnoreCase(token.getUserName())){
            procReqInstance.killIt();
            LPFrontEnd.servletReturnResponseError(request, response, 
                    LPPlatform.ApiErrorTraping.INVALID_TOKEN.getErrorCode(), null, language, LPPlatform.ApiErrorTraping.class.getSimpleName());
            return;                             
        }
        if (Boolean.FALSE.equals(LPFrontEnd.servletStablishDBConection(request, response))){return;}          
        try{
            switch (endPoint){
                case GET_ALL_HOLIDAY_DATES_LIST_ALL_CALENDARS:              
                    String[] fieldsToRetrieveCalendar=getAllFieldNames(TblsApp.TablesApp.HOLIDAYS_CALENDAR);
                    String[] fieldsToRetrieveCalendarDate=getAllFieldNames(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE);
                    Object[][] calendarInfo=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.HOLIDAYS_CALENDAR,
                            EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.HOLIDAYS_CALENDAR, "ALL"),                            
                            new String[]{TblsApp.HolidaysCalendar.ACTIVE.getName()}, 
                            new Object[]{true}, 
                            new String[]{TblsApp.HolidaysCalendar.CODE.getName()});
                    JSONArray jCalendarsArr = new JSONArray();
                    if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(calendarInfo[0][0].toString())){
                        for (Object[] currCalendar: calendarInfo){
                            Object curCalendarCode=currCalendar[LPArray.valuePosicInArray(fieldsToRetrieveCalendar, TblsApp.HolidaysCalendar.CODE.getName())];
                            JSONObject jObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveCalendar, currCalendar);
                            Object[][] calendarDateInfo=QueryUtilitiesEnums.getTableData(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE,
                                    EnumIntTableFields.getTableFieldsFromString(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE, "ALL"),                            
                                    new String[]{TblsApp.HolidaysCalendarDate.CALENDAR_CODE.getName()}, 
                                    new Object[]{curCalendarCode}, 
                                    new String[]{TblsApp.HolidaysCalendarDate.CALENDAR_CODE.getName(), TblsApp.HolidaysCalendarDate.ID.getName()});
                            JSONArray jDatesArr = new JSONArray();
                            if (!LPPlatform.LAB_FALSE.equalsIgnoreCase(calendarDateInfo[0][0].toString())){
                                for (Object[] currCalendarDate: calendarDateInfo){
                                    JSONObject jCalDateObj=LPJson.convertArrayRowToJSONObject(fieldsToRetrieveCalendarDate, currCalendarDate);
                                    Object currCalendarDateDateObj=currCalendarDate[LPArray.valuePosicInArray(fieldsToRetrieveCalendarDate, TblsApp.HolidaysCalendarDate.DATE.getName())];                                    
                                    LocalDateTime stringFormatToDate = LPDate.stringFormatToLocalDateTime(currCalendarDateDateObj.toString());
                                    jCalDateObj.put("date_year", stringFormatToDate.getYear());
                                    jCalDateObj.put("date_month", stringFormatToDate.getMonthValue());
                                    jCalDateObj.put("date_dayOfMonth", stringFormatToDate.getDayOfMonth());
                                    jDatesArr.add(jCalDateObj);
                                }
                            }
                            jObj.put(TblsApp.TablesApp.HOLIDAYS_CALENDAR_DATE.getTableName(), jDatesArr);
                            jCalendarsArr.add(jObj);
                        }
                    }
                    LPFrontEnd.servletReturnSuccess(request, response, jCalendarsArr);
                    return;
                default: 
            }
        }catch(Exception e){   
            procReqInstance.killIt();
            String[] errObject = new String[]{e.getMessage()};
            LPFrontEnd.responseError(errObject, language, null);
        } finally {
            // release database resources
            try {           
                procReqInstance.killIt();
            } catch (Exception ex) {Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }          
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            processRequest(request, response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
