package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.httpserver.model.HttpServletRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.MonitoringService;

public class MonitorServlet extends MyHttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		MonitoringService monitoringService = Application.getInstance().getMonitoringService();

		if( !validateAuth()){
			return;
		} 
		
		StringBuffer buf = response.getBuffer();
		buf.append("<html>");
		buf.append("<head>");
		buf.append("<style>");
		buf.append("td { padding: 5px; }");
		buf.append("td.value { text-align: center }");
		buf.append("</style>");
		buf.append("</head>");
		
		buf.append("<body>");

		buf.append("<h1>CSSC Proxy Server Monitoring</h1>");
		buf.append("<hr/>");
		
		buf.append("<a href=\"/\">Back</a>");

		buf.append("<h2>Monitoring parameters</h2>");
		
		buf.append("<h3>Transferred bytes</h3>");
		buf.append("<table>");
		
		buf.append("<tr>");
		buf.append("<th>Parameter</th>");
		buf.append("<th>Value</th>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Total transferred bytes</td>");
		buf.append("<td class=\"value\">" + monitoringService.getTotalTransferredBytes() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Bytes sent from client to proxy</td>");
		buf.append("<td class=\"value\">" + monitoringService.getClientReceivedTransferredBytes() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Bytes sent from proxy to origin server</td>");
		buf.append("<td class=\"value\">" + monitoringService.getServerSentTransferredBytes() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Bytes sent from origin server to proxy</td>");
		buf.append("<td class=\"value\">" + monitoringService.getServerReceivedTransferredBytes() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Bytes sent from proxy to client</td>");
		buf.append("<td class=\"value\">" + monitoringService.getClientSentTransferredBytes() + "</td>");
		buf.append("</tr>");
		
		buf.append("</table>");
		
		
		buf.append("<h3>Blocks</h3>");
		buf.append("<table>");
		
		buf.append("<tr>");
		buf.append("<th>Parameter</th>");
		buf.append("<th>Value</th>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Whole blocks</td>");
		buf.append("<td class=\"value\">" + monitoringService.getWholeBlocks() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>IP blocks</td>");
		buf.append("<td class=\"value\">" + monitoringService.getIpBlocks() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>URI blocks</td>");
		buf.append("<td class=\"value\">" + monitoringService.getUriBlocks() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Media type blocks</td>");
		buf.append("<td class=\"value\">" + monitoringService.getMediaTypeBlocks() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Size blocks</td>");
		buf.append("<td class=\"value\">" + monitoringService.getSizeBlocks() + "</td>");
		buf.append("</tr>");
		
		buf.append("</table>");
		
		
		buf.append("<h3>Transformations</h3>");
		buf.append("<table>");
		
		buf.append("<tr>");
		buf.append("<th>Parameter</th>");
		buf.append("<th>Value</th>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>L33t transformations</td>");
		buf.append("<td class=\"value\">" + monitoringService.getLeetTransformations() + "</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Image rotations</td>");
		buf.append("<td class=\"value\">" + monitoringService.getImage180Transformations() + "</td>");
		buf.append("</tr>");
		
		buf.append("</table>");

		buf.append("</body></html>");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException {
		doGet(request, response);
	}

}
