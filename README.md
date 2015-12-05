# TwitterMap
Use the Elastic Beanstalk API to create, configure, and deploy an application instance programmatically.         
Use the Elastic LoadBalancing API to configure load balancing on Elastic Beanstalk created.         
           
For this assignment you will develop an application that:         
          
Reads a stream of tweets from the Twitter Live API (Code provided)              
Records the tweet ID, time, and other relevant elements into a DB (SQL or NoSQL)             
Presents the Tweet in a map that is being updated in Near Real Time (Consider evaluating WebSockets, or Server Side Events for your implementation)          
The map clusters tweets as to show where is people tweeting the most, according to the sample tweets you get from the streaming API.           
             
Here are some steps:              
1.Collect about 100MB twits using Twitter API.            
2.Parse the Twits and store in Dataset. The parsed twits should have location information and a set of key words from the content of the twits.               
3.You create a scatter plot or any nice plot that depicts all the twits with a the density map - perhaps with color gradient etc. (extra credit - 10 points for nice visualization)            
4.You should provide a filter that allows a drop down keywords to choose from and only shows twits with those keywords on a google map.                        
5.Categories of your choosing, show what is trending and where in that category. Example categories could be News, Music, Person etc.      

![architecture](https://cloud.githubusercontent.com/assets/10342877/11604814/744de148-9abf-11e5-9a78-42b9d5e0cdc2.png)   


Function Description:
1.When we open website, it will send ajax call to TwitterFetchServlet to start to crawl tweets using Twitter Stream API.          
2.We will save them into database and at the same time, we will store this tweets into JsonArray, and transform into heatmap.js, parse it and display them into view page.           
3.We also set a timer to get json data of all tweets from database periodly, and sort it by different categories(keywords) in heatmap.js        

![TwitterMap](https://cloud.githubusercontent.com/assets/10342877/11601916/972aee74-9aa4-11e5-9237-0a84a7c5c211.png)     
