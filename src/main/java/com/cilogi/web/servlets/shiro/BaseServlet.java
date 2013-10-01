// Copyright (c) 2011 Tim Niblett All Rights Reserved.
//
// File:        BaseServlet.java  (31-Oct-2011)
// Author:      tim

//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used,
// sold, licenced, transferred, copied or reproduced in whole or in
// part in any manner or form or in or on any media to any person
// other than in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.cilogi.web.servlets.shiro;

import com.cilogi.shiro.gaeuser.IGaeUserDAO;
import com.cilogi.util.MimeTypes;
import com.cilogi.web.servlets.shiro.view.IRenderView;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;


public class BaseServlet extends HttpServlet {
    static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());

    private static final long serialVersionUID = 7427222103993326328L;

    protected final String MESSAGE = "message";
    protected final String CODE = "code";

    protected final int HTTP_STATUS_OK = 200;
    protected final int HTTP_STATUS_NOT_FOUND = 404;
    protected final int HTTP_STATUS_FORBIDDEN = 403;
    protected final int HTTP_STATUS_INTERNAL_SERVER_ERROR = 500;

    private IRenderView renderView;

    @Getter
    protected IGaeUserDAO gaeUserDAO;

    protected BaseServlet(IGaeUserDAO gaeUserDAO) {
        this.gaeUserDAO = gaeUserDAO;
    }


    @Inject
    protected void setRenderView(IRenderView renderView) {
        this.renderView = renderView;
    }

    protected void issue(String mimeType, int returnCode, String output, HttpServletResponse response) throws IOException {
        response.setContentType(mimeType);
        response.setStatus(returnCode);
        response.getWriter().println(output);
    }

    protected void issueJson(HttpServletResponse response, int status, Object... args) throws IOException {
        Preconditions.checkArgument(args.length % 2 == 0, "There must be an even number of strings");
            try {
            JSONObject obj = new JSONObject();
            for (int i = 0; i < args.length; i += 2) {
                obj.put((String)args[i], args[i+1]);
            }
            issueJson(response, status, obj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    protected void issueJson(HttpServletResponse response, int status, JSONObject obj) throws IOException {
        issue(MimeTypes.MIME_APPLICATION_JSON, status, obj.toString(), response);
    }

    protected void showView(HttpServletResponse response, String templateName) throws IOException {
        renderView.defaultRender(null, response, templateName);
     }

    protected void showView(HttpServletResponse response, String templateName, Map<String,Object> args) throws IOException {
        renderView.render(null, response, templateName, args);
     }

    protected int intParameter(String name, HttpServletRequest request, int deflt) {
        String s = request.getParameter(name);
        return (s == null) ? deflt : Integer.parseInt(s);
    }

    /**
     * Login and make sure you then have a new session.  This helps prevent session fixation attacks.
     *
     * @param token  The token
     * @param subject The subject
     */
    protected static void loginWithNewSession(AuthenticationToken token, Subject subject) {
        Session originalSession = subject.getSession();

        Map<Object, Object> attributes = Maps.newLinkedHashMap();
        Collection<Object> keys = originalSession.getAttributeKeys();
        for(Object key : keys) {
            Object value = originalSession.getAttribute(key);
            if (value != null) {
                attributes.put(key, value);
            }
        }
        originalSession.stop();
        subject.login(token);

        Session newSession = subject.getSession();
        for(Object key : attributes.keySet() ) {
            newSession.setAttribute(key, attributes.get(key));
        }
    }

    protected boolean isCurrentUserAdmin() {
        Subject subject = SecurityUtils.getSubject();
        return subject.hasRole("admin");
    }

    protected void setProviderInCookieComment(String provider) {
        SecurityManager man = SecurityUtils.getSecurityManager();
        if (man != null && man instanceof DefaultWebSecurityManager) {
            DefaultWebSecurityManager sm = (DefaultWebSecurityManager)man;
            RememberMeManager rm = sm.getRememberMeManager();
            if (rm instanceof CookieRememberMeManager) {
                CookieRememberMeManager cm = (CookieRememberMeManager)rm;
                Cookie cookie = cm.getCookie();
                cookie.setComment(provider);
            }
        }
    }

    protected String getProviderInCookieComment() {
        SecurityManager man = SecurityUtils.getSecurityManager();
        if (man != null && man instanceof DefaultWebSecurityManager) {
            DefaultWebSecurityManager sm = (DefaultWebSecurityManager)man;
            RememberMeManager rm = sm.getRememberMeManager();
            if (rm instanceof CookieRememberMeManager) {
                CookieRememberMeManager cm = (CookieRememberMeManager)rm;
                Cookie cookie = cm.getCookie();
                return cookie.getComment();
            }
        }
        return "";
    }
}