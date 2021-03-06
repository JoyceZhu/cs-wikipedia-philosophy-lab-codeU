package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
	
	final static WikiFetcher wf = new WikiFetcher();
    final static String philosophy_url = "https://en.wikipedia.org/wiki/Philosophy";
    static List<String> visited = new ArrayList<String>();
    static int parensCount = 0;
	
	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 * 
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 * 
	 * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
        // some example code to get you started

		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        // Start out by adding the initial URL.
        visited.add(url);
		depthFirstParse(url);
	}

    public static void depthFirstParse (String url) throws IOException{
        System.out.println("Parsing " + url);
        if (visited.contains(url)) return;
        if (url.equals(philosophy_url)) // Finished, print out links
        {   
            System.out.println(visited.size() + "pages taken to reach the Philosophy Wikipedia page:");
            for (String visitedUrl : visited)
                System.out.println(visitedUrl);
            return;
        }
		Elements paragraphs = wf.fetchWikipedia(url);
        for (int i = 0; i < paragraphs.size(); i++)
        {
		    Element currentParagraph = paragraphs.get(i);
		    Iterable<Node> iter = new WikiNodeIterable(currentParagraph);
		    for (Node node: iter) {
			    if (node instanceof TextNode) {
                    // Count number of unbalanced parens to avoid links in parens.
				    if (node.toString().contains("(")) parensCount++;
                    if (node.toString().contains(")")) parensCount--;
			    }
                if (parensCount == 0)
                {
                    if (node instanceof Element)
                    {
                        Element current = (Element) node;
                        if (current.tagName().equals("a"))
                        {
                            if (isValid(current, url))
                            {
                                String next = current.attr("abs:href");
                                visited.add(next);
                                depthFirstParse(next);
                                return;
                            }
                        }
                    }
                }
            }
        }
        System.err.println("No valid links found");
        return;
    }

    /* No italics or links to the current page. */
    public static boolean isValid(Element e, String url)
    {
        String next = e.attr("abs:href");
        if (url.equals(next)) // links to current page
            return false;
        Element parent = (Element) e.parentNode();
        // italics
        if (parent.tagName().equals("i") || parent.tagName().equals("em"))
            return false;
        return true;
    }
}
