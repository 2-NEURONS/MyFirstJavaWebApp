package net.myfirst.webapp;

import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

// class definition
public class HelloWorld
{


    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    public static void main(String[] args)
    {

        port(getHerokuAssignedPort());
        System.out.println("------");
        Map<String,Integer> useCounter = new HashMap<>();// store users and their counts in key-value pairs
        staticFiles.location("/public"); // for displaying the main page using pure html
        //
        get("home", (request, response) -> // route a user to the main page
        {

            Map<String, Object> map = new HashMap<>(); // local map to pass values to the handlebars
            map.put("count", useCounter.keySet().size()); // storing a user and its count
            return new ModelAndView(map, "index.handlebars");

        }, new HandlebarsTemplateEngine());

        get("/greeted/:username", (req, res) -> // display a user and its count
        {
            Map<String, Object> map = new HashMap<>();
            String userName = req.params(":username"); // get a query string from the link
            int userCounter = useCounter.get(userName);
            map.put("count",userCounter); // store a count
            map.put("user",userName); // store a user

            return new ModelAndView(map, "greetedUser.handlebars");

        }, new HandlebarsTemplateEngine());

        get("/greeted", (req, res) -> // route a user to a display ofall users
        {
            Map<String, Object> map = new HashMap<>();
            map.put("users",useCounter);
            return new ModelAndView(map, "greeted.handlebars");
        }, new HandlebarsTemplateEngine());


        post("/greet", (req, res) -> // greet users
        {
            Map<String, Object> map = new HashMap<>(); // local map for passing data to my handlebar
            String greeting = null; // to store greeting message
            String userName = req.queryParams("username"); // username from query string

            if (userName.length() > 0 )
            {
                if( useCounter.containsKey(userName))
                {
                    int somethingLikethat =  useCounter.get(userName)+1;
                    useCounter.put(userName,somethingLikethat);
                }
                else
                {
                    useCounter.put(userName,1);
                }
                String lang =  req.queryParams("language");
                greeting = (lang.equals("xhosa")? ("Molo, " + userName) : (lang.equals("shona"))? ("Mhoro, " +userName): (lang.equals("swahili"))? ("hujambo, " + userName) : "Hello, " + userName );
                map.put("greeting", greeting);
                map.put("count", useCounter.keySet().size());
                return new ModelAndView(map, "greet.handlebars");

            }
            else
            {
                greeting = "Nothing-Fancy";
                map.put("greeting", greeting);
                map.put("count", useCounter.keySet().size());
                return new ModelAndView(map, "greet.handlebars");
            }

        }, new HandlebarsTemplateEngine());

    }

} // end of Hello world