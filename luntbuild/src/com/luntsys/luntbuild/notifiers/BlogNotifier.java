package com.luntsys.luntbuild.notifiers;

import com.luntsys.luntbuild.db.Build;
import com.luntsys.luntbuild.db.Schedule;
import com.luntsys.luntbuild.db.User;
import com.luntsys.luntbuild.utility.Luntbuild;
import com.luntsys.luntbuild.utility.NotifierProperty;

import org.apache.tools.ant.Project;

import org.apache.xmlrpc.XmlRpcClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Blog Notifier
 *
 * @author Lubos Pochman
 *
 */
public class BlogNotifier extends TemplatedNotifier {
    /**
     * Keep tracks of version of this class, used when do serialization-deserialization
     */
    static final long serialVersionUID = 1L;
    /**
     * Indices for notifier properties
     */
    private static final int BLOG_TYPE     = 0;
    private static final int BLOG_URL      = 1;
    private static final int BLOG_ID       = 2;
    private static final int BLOG_USER     = 3;
    private static final int BLOG_PASSWORD = 4;
    private static final int BLOG_CATEGORY = 5;

    private static final SimpleDateFormat TIME_DISPLAY_FORMAT =
        new SimpleDateFormat("HH:mm.ssss");

    private static HashMap blogTypes = new HashMap();
    static {
        blogTypes.put("blogger", BloggerSender.class);
        blogTypes.put("livejournal", LiveJournalSender.class);
        blogTypes.put("metaweblog", MetaWeblogSender.class);
    }

    /**
     * Constructor
     */
    public BlogNotifier() {
        super(BlogNotifier.class, "blog");
    }

    public String getDisplayName() {
        return "Blog";
    }

    /** Send blog
     * @param blog blog
     * @param antProject project
     * @param subject subject
     * @param body body
     */
    private void sendBlog(BlogConnection blog, Project antProject, String subject, String body) {
        /* Prepare blog message */
        antProject.log("Send build notification via blog to: " + blog, Project.MSG_INFO);
        try {
            /* Send blog */
            BlogSender sender = getSender(blog.getType(), antProject);
            if (sender != null) {
                Object postId = sender.post(blog, subject, body, antProject);
                if (postId != null) {
                    antProject.log("Blog entry " + postId + " created at " + blog.getUrl(), Project.MSG_INFO);
                } else {
                    antProject.log("Blog entry ID not available from " + blog.getUrl(), Project.MSG_DEBUG);
                }
            } else {
                super.logger.error("No blog type associated with '" + blog.getType() + "'");
                antProject.log("No blog type associated with '" + blog.getType() + "'", Project.MSG_ERR);
            }
        } catch (Exception e) {
            antProject.log(Luntbuild.getExceptionMessage(e), Project.MSG_ERR);
        }
    }

    public void sendBuildNotification(Set checkinUsers, Set subscribeUsers, Build build, Project antProject) {
        Iterator it = checkinUsers.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            BlogConnection blog = getBlogConnection(user);
            if (Luntbuild.isEmpty(blog.getUrl()))
                antProject.log("Cannot send blog to user \"" +
                        user.getName() + "\": blog url is empty!", Project.MSG_WARN);
            else
                sendBlog(blog, antProject, constructNotificationTitle(build),
                        constructNotificationBody4CheckinUsers(build));
        }
        it = subscribeUsers.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            BlogConnection blog = getBlogConnection(user);
            if (Luntbuild.isEmpty(blog.getUrl()))
                antProject.log("Cannot send blog to user \"" +
                        user.getName() + "\": blog is empty!", Project.MSG_WARN);
            else
                sendBlog(blog, antProject, constructNotificationTitle(build),
                        constructNotificationBody(build));
        }
    }

    public void sendScheduleNotification(Set subscribeUsers, Schedule schedule, Project antProject) {
        Iterator it = subscribeUsers.iterator();
        while (it.hasNext()) {
            User user = (User) it.next();
            BlogConnection blog = getBlogConnection(user);
            if (Luntbuild.isEmpty(blog.getUrl()))
                antProject.log("Cannot send blog to user \"" +
                        user.getName() + "\": blog is empty!", Project.MSG_WARN);
            else
                sendBlog(blog, antProject, constructNotificationTitle(schedule),
                        constructNotificationBody(schedule));
        }
    }

    private BlogConnection getBlogConnection(User user) {
        BlogConnection blog = new BlogConnection(
                ((NotifierProperty)getUserLevelProperties().get(BLOG_TYPE)).getValue(user.getContacts()),
                ((NotifierProperty)getUserLevelProperties().get(BLOG_URL)).getValue(user.getContacts()),
                ((NotifierProperty)getUserLevelProperties().get(BLOG_ID)).getValue(user.getContacts()),
                ((NotifierProperty)getUserLevelProperties().get(BLOG_USER)).getValue(user.getContacts()),
                ((NotifierProperty)getUserLevelProperties().get(BLOG_PASSWORD)).getValue(user.getContacts()),
                ((NotifierProperty)getUserLevelProperties().get(BLOG_CATEGORY)).getValue(user.getContacts())
                );
        return blog;
    }

    private BlogSender getSender(String apiName, Project antProject) {
        Class impl = (Class) blogTypes.get(apiName.toLowerCase());
        if (impl != null) {
            try {
                return (BlogSender) impl.newInstance();
            } catch (Exception e) {
                super.logger.error("Failed to instantiate BloggSender "+ impl.getName() + ", due to a " +
                                e.getClass().getName() + ": " + e.getMessage());
                antProject.log(Luntbuild.getExceptionMessage(e), Project.MSG_ERR);
            }
        }
        return null;
    }

    /**
     * @see com.luntsys.luntbuild.notifiers.Notifier#getSystemLevelProperties()
     */
    public List getSystemLevelProperties() {
        List properties = new ArrayList();
        return properties;
    }

    /**
     * @return List of NotifierProperty
     * @see com.luntsys.luntbuild.notifiers.Notifier#getUserLevelProperties()
     */
    public List getUserLevelProperties() {
        List properties = new ArrayList();

        properties.add(new NotifierProperty() {
            public Class getNotifierClass() {
                return BlogNotifier.class;
            }

            public String getDisplayName() {
                return "Blog Type";
            }

            public String getDescription() {
                return "Specify Blog Type. Supported blog types are: blogger or livejournal or metaweblog";
            }
        });

        properties.add(new NotifierProperty() {
            public Class getNotifierClass() {
                return BlogNotifier.class;
            }

            public String getDisplayName() {
                return "Blog URL";
            }

            public String getDescription() {
                return "Specify URL for your blog. For example http://www.blogger.com/api for blogger type, " +
                    "http://jroller.com/xmlrpc for metaweblog type, " +
                    "and http://www.livejournal.com/interface/xmlrpc for livejournal type.";
            }
        });

        properties.add(new NotifierProperty() {
            public Class getNotifierClass() {
                return BlogNotifier.class;
            }

            public String getDisplayName() {
                return "Blog ID";
            }

            public String getDescription() {
                return "Specify ID for your blog. Only used for blogger type.";
            }
        });

        properties.add(new NotifierProperty() {
            public Class getNotifierClass() {
                return BlogNotifier.class;
            }

            public String getDisplayName() {
                return "Blog User";
            }

            public String getDescription() {
                return "Blog User to use to access your blog.";
            }
        });

        properties.add(new NotifierProperty() {
            public Class getNotifierClass() {
                return BlogNotifier.class;
            }

            public boolean isSecret(){
                return true;
            }

            public String getDisplayName() {
                return "Blog Password";
            }

            public String getDescription() {
                return "Blog Password to use to access your blog.";
            }
        });

        properties.add(new NotifierProperty() {
            public Class getNotifierClass() {
                return BlogNotifier.class;
            }

            public String getDisplayName() {
                return "Blog Category";
            }

            public String getDescription() {
                return "Blog Category to use to send the blog (comma separated list). Only used for metaweblog type.";
            }
        });

        return properties;
    }

    class BlogConnection {
        private String type;
        private String url;
        private String id;
        private String user;
        private String password;
        private String category;

        BlogConnection(String type, String url, String id, String user, String password, String category) {
            this.type = type;
            this.url = url;
            this.id = id;
            this.user = user;
            this.password = password;
            this.category = category;
        }

        /**
         * @return Returns the category.
         */
        public String getCategory() {
            return this.category;
        }

        /**
         * @return Returns the password.
         */
        public String getPassword() {
            return this.password;
        }

        /**
         * @return Returns the type.
         */
        public String getType() {
            return this.type;
        }

        /**
         * @return Returns the url.
         */
        public String getUrl() {
            return this.url;
        }

        /**
         * @return Returns the user.
         */
        public String getUser() {
            return this.user;
        }

        /**
         * @return Returns the id.
         */
        public String getId() {
            return (this.id != null && this.id.trim().length() > 0) ? this.id : "luntbuild";
        }

        /**
         * @param id The id to set.
         */
        public void setId(String id) {
            this.id = id;
        }
    }

    /**
     * The blogging type sender interface.
     *
     * @author lubosp
     */
    interface BlogSender {
        /**
         * Post a new blog entry.
         * @param blog blog connection.
         * @param subject blog entry's subject.
         * @param content blog entry's content.
         * @param antProject project.
         * @return The posted blog id.
         */
        public Object post(BlogConnection blog, String subject, String content, Project antProject);
    }

    /**
     * Implementation of the Blogger.
     */
    public static class BloggerSender implements BlogSender {

        public Object post(BlogConnection blog, String subject, String content, Project antProject) {
            content = "<title>" + subject + "</title>" + content;
            Object postId = null;
            try {
                XmlRpcClient xmlrpc = new XmlRpcClient(blog.getUrl());
                Vector params = new Vector();
                params.add("Luntbuild Blog Sender");
                params.add(blog.getId());
                params.add(blog.getUser());
                params.add(blog.getPassword());
                params.add(content);
                params.add(Boolean.TRUE);
                postId = xmlrpc.execute("blogger.newPost", params);
            } catch (Exception e) {
                antProject.log(Luntbuild.getExceptionMessage(e), Project.MSG_ERR);
            }
            return postId;
        }
    }

    /**
     * Implementation of the MetaWeblog.
     */
    public static class MetaWeblogSender implements BlogSender {

        public Object post(BlogConnection blog, String subject, String content, Project antProject) {
            Object postId = null;
            try {
                XmlRpcClient xmlrpc = new XmlRpcClient(blog.getUrl());
                Vector params = new Vector();
                params.add(blog.getId());
                params.add(blog.getUser());
                params.add(blog.getPassword());

                // MetaWeblogAPI expects the blog entry data elements in an
                // internal map-structure unlike Blogger API does.
                Hashtable struct = new Hashtable();
                struct.put("title", subject);
                struct.put("description", content);
                Vector categories = new Vector();
                if (blog.getCategory() != null) {
                    StringTokenizer tok = new StringTokenizer(blog.getCategory(), ",");
                    while (tok.hasMoreTokens()) {
                        categories.add(tok.nextToken().trim());
                    }
                }
                struct.put("categories", categories);

                params.add(struct);
                params.add(Boolean.TRUE);
                postId = xmlrpc.execute("metaWeblog.newPost", params);
            } catch (Exception e) {
                antProject.log(Luntbuild.getExceptionMessage(e), Project.MSG_ERR);
            }
            return postId;
        }
    }

    /**
     * Implementation of the LiveJournal.
     */
    public static class LiveJournalSender implements BlogSender {

        private String stripLineFeeds(String input) {
            StringBuffer s = new StringBuffer();
            char[] chars = input.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] != '\n' && chars[i] != '\r') {
                    s.append(chars[i]);
                }
            }
            return s.toString();
        }

        public Object post(BlogConnection blog, String subject, String content, Project antProject) {
            Object postId = null;
            try {
                XmlRpcClient xmlrpc = new XmlRpcClient(blog.getUrl());
                Vector params = new Vector();
                Hashtable struct = new Hashtable();
                struct.put("username", blog.getUser());

                struct.put("auth_method", "clear");
                struct.put("password", blog.getPassword());

                struct.put("subject", subject);
                struct.put("event", stripLineFeeds(content));
                struct.put("lineendings", "\n");
                struct.put("security", "public");
                Calendar now = Calendar.getInstance();
                struct.put("year", "" + now.get(Calendar.YEAR));
                struct.put("mon", "" + (now.get(Calendar.MONTH) + 1));
                struct.put("day", "" + now.get(Calendar.DAY_OF_MONTH));
                struct.put("hour", "" + now.get(Calendar.HOUR_OF_DAY));
                struct.put("min", "" + now.get(Calendar.MINUTE));
                params.add(struct);
                postId = xmlrpc.execute("LJ.XMLRPC.postevent", params);
            } catch (Exception e) {
                antProject.log(Luntbuild.getExceptionMessage(e), Project.MSG_ERR);
            }
            return postId;
        }
    }


}
