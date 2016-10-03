package com.openstack.rest;

import java.io.File;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.User;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.telemetry.Statistics;

import com.openstack.Meter;
import com.openstack.service.OpenstackCompute;


@Path("/compute")
public class OpenstackComputeEndPoint {
	
	private final static String USERNAME = "pavani";
	private final static String PASSWORD = "pavani";

	@POST
	@Path("/servers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createVM(@QueryParam("vmName") String VMName){
		OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		Server server = keystone.createVM(VMName);
		System.out.println(server);
		//List<? extends Server> servers = keystone.getServers();
		//GenericEntity<List<? extends Server>> entity = new GenericEntity<List<? extends Server>>(servers) {};
		GenericEntity<Server> entity = new GenericEntity<Server>(server) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@GET
	@Path("/servers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServers(){
		OpenstackCompute keystone = new OpenstackCompute("admin","b72f2e25bab64b2c");
		//OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		List<? extends Server> servers = keystone.getServers();
		GenericEntity<List<? extends Server>> entity = new GenericEntity<List<? extends Server>>(servers) {};
		return Response.status(Status.OK).entity(entity).build();
	}

	@POST
	@Path("/upload")
	public Response uploadFile(File file, @QueryParam("fileName") String fileName){
		OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		//readFile(fileName);
		keystone.uploadFile(file,fileName);
		List<? extends SwiftObject> objects = keystone.getObjects();
		GenericEntity<List<? extends SwiftObject>> entity = new GenericEntity<List<? extends SwiftObject>>(objects) {};
		return Response.status(Status.OK).entity(entity).build();
	}

	@GET
	@Path("/download/{fileName}")
	public Response downloadFile(@PathParam("fileName") String fileName){
		OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		//readFile(fileName);
		File file = keystone.downloadFile(fileName);
		return Response.ok((Object) file).header("Content-Disposition",
				"attachment; filename=\""+fileName+"\"").build();
	}

	@GET
	@Path("/objects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getObjects(){
		OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		List<? extends SwiftObject> objects = keystone.getObjects("Container1");
		GenericEntity<List<? extends SwiftObject>> entity = new GenericEntity<List<? extends SwiftObject>>(objects) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	
	@GET
	@Path("/containers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContainers(){
		OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		List<? extends SwiftContainer> objects = keystone.getContainers();
		GenericEntity<List<? extends SwiftContainer>> entity = new GenericEntity<List<? extends SwiftContainer>>(objects) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@GET
	@Path("/containers/{containerName}/objects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getContainerObject(@PathParam("containerName")String containerName){
		OpenstackCompute keystone = new OpenstackCompute(USERNAME,PASSWORD);
		List<? extends SwiftObject> objects = keystone.getObjects(containerName);
		GenericEntity<List<? extends SwiftObject>> entity = new GenericEntity<List<? extends SwiftObject>>(objects) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@QueryParam("userName") String userName, @QueryParam("password") String password){
		OpenstackCompute keystone = new OpenstackCompute();
		User user = keystone.getUser(userName);
		GenericEntity<User> entity = new GenericEntity<User>(user) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getuser(){
		OpenstackCompute keystone = new OpenstackCompute();
		User user = keystone.getUser();
		GenericEntity<User> entity = new GenericEntity<User>(user) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response register(@QueryParam("userName") String userName, @QueryParam("password") String password, @QueryParam("emailId") String emailId){
		OpenstackCompute keystone = new OpenstackCompute();
		User user = keystone.createUser(userName, password, emailId);
		GenericEntity<User> entity = new GenericEntity<User>(user) {};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@GET
	@Path("/outGoingMeter")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMeters(){
		OpenstackCompute keystone = new OpenstackCompute();
		Statistics stats = keystone.getObjectOutgoingBytes();
		Statistics inStats = keystone.getObjectIncomingBytes();
		
		//Meter meter = new Meter((float)(double)stats.getMax(), (float)(double)inStats.getMax());
		Meter meter = new Meter((float)(double)stats.getSum(), (float)(double)inStats.getSum());
		GenericEntity<Meter> entity = new GenericEntity<Meter>(meter) {};
		return Response.status(Status.OK).entity(entity).build();
	}
}
