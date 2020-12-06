package com.laioffer.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laioffer.job.db.MySQLConnection;
import com.laioffer.job.entity.Item;
import com.laioffer.job.entity.ResultResponse;
import com.laioffer.job.external.GitHubClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        // why here still need mapper is jackson
        ObjectMapper mapper = new ObjectMapper();
        HttpSession httpSession = request.getSession(false);
        if(httpSession == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }
        // directly you will get string
        String userId = request.getParameter("user_id");
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemsIds = connection.getFavoriteItemIds(userId);
        connection.close();
        GitHubClient client = new GitHubClient();
        List<Item> itemsString = client.search(lat, lon, null);

        for(Item item: itemsString) {
            item.setFavorite(favoritedItemsIds.contains(item.getId()));
        }



        // nearly no difference is print and write
        response.getWriter().print(mapper.writeValueAsString(itemsString));
    }
}
