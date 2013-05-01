package com.joshdholtz.trajectory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class Trajectory {
	
	Context context;
	
	Map<String, Route> routes;
	List<PatternRoute> patternRoutes;

	private Trajectory() {
		super();
		routes = new HashMap<String, Route>();
		patternRoutes = new ArrayList<PatternRoute>();
	}
	
	private static Trajectory getInstance() {
		return LazyHolder.instance;
	}

	private static class LazyHolder {
		private static Trajectory instance = new Trajectory();
	}
	
	public static void setCurrentActivityContext(Context context) {
		synchronized (LazyHolder.instance) {
			Trajectory.getInstance().context = context;
		}
	}
	
	public static void removeCurrentActivityContext(Context context) {
		synchronized (LazyHolder.instance) {
			if (Trajectory.getInstance().context == context) {
				Trajectory.getInstance().context = null;
			}
		}
	}
	
	public static Context getCurrentActivityContext() {
		synchronized (LazyHolder.instance) {
			return Trajectory.getInstance().context;
		}
	}
	
	public static void setRoute(String routePattern, Route route) {
		Trajectory.getInstance()._setRoute(routePattern, route);
	}
	
	private void _setRoute(String routePattern, Route route) {
		routes.put(routePattern, route);
	}
	
	public static void setRoute(Pattern routePattern, Route route) {
		Trajectory.getInstance()._setRoute(routePattern, route);
	}
	
	private void _setRoute(Pattern routePattern, Route route) {
		patternRoutes.add(new PatternRoute(routePattern, route));
	}
	
	public static void call(String route) {
		Trajectory.getInstance()._call(route);
	}
	
	private void _call(String route) {
		List<String> matches = new ArrayList<String>();
		Map<String, String> queryParams = new HashMap<String, String>();
		
		Route r = routes.get(route);
		if (r != null) {
			r.onRoute(route, matches, queryParams);
			return;
		}
		
		for (PatternRoute patternRoute : patternRoutes) {
			Matcher matcher = patternRoute.pattern.matcher(route);
			
			boolean found = false;
			while (matcher.find()) {
				for (int i = 1; i < matcher.groupCount() + 1; ++i) {
					matches.add(matcher.group(i));
				}
				
				found = true;
			}
			
			if (found) {
				patternRoute.route.onRoute(route, matches, queryParams);
				return;
			}
			
//			if (matcher.matches()) {
//				
//			}
		}
	}
	
	private class PatternRoute {
		
		Pattern pattern;
		Route route;
		
		public PatternRoute(Pattern pattern, Route route) {
			super();
			this.pattern = pattern;
			this.route = route;
		}
		
	}
	
	public abstract static class Route {
		public abstract void onRoute(String route, List<String> routeParams, Map<String, String> queryParams);
	}
	
}
