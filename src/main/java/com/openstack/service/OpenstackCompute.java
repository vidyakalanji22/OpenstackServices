package com.openstack.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.identity.UserService;
import org.openstack4j.api.types.Facing;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.DLPayload;
import org.openstack4j.model.common.Payload;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.identity.Role;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.identity.User;
import org.openstack4j.model.image.ContainerFormat;
import org.openstack4j.model.image.DiskFormat;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.AttachInterfaceType;
import org.openstack4j.model.network.IPVersionType;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.Router;
import org.openstack4j.model.network.RouterInterface;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.telemetry.SampleCriteria;
import org.openstack4j.model.telemetry.SampleCriteria.Oper;
import org.openstack4j.model.telemetry.Statistics;
import org.openstack4j.openstack.OSFactory;

public class OpenstackCompute {
	private OSClient os=null;
	private final static String USERNAME = "pavani";
	private final static String PASSWORD = "pavani";
	
	public OpenstackCompute(){
		System.out.println("in constructor");
		os = OSFactory.builder()
				.endpoint("http://localhost:5000/v2.0")
				.credentials("admin","b72f2e25bab64b2c")
				.withConfig(Config.newConfig().withEndpointNATResolution("127.0.0.1")) 
				.tenantName("admin")
				.authenticate();
		
		os.perspective(Facing.ADMIN);
		System.out.println("OS Client "+os);
	}
	
	public OpenstackCompute(String userName, String password){
		os = OSFactory.builder()
				.endpoint("http://localhost:5000/v2.0")
				.credentials(userName,password)
				.withConfig(Config.newConfig().withEndpointNATResolution("127.0.0.1")) 
				.tenantName(userName)
				.authenticate();
	}
	
	public User getUser(String userName){
		User user = os.identity().users().getByName(userName);
		return user;
	}
	
	public User getUser(){
		User user = os.identity().users().getByName(USERNAME);
		return user;
	}


	private String createFlavor(){

		Flavor flavor = os.compute().flavors()
				.create("mini_v4", 1024, 1, 3, 1, 1, 1.2f, true); 
		System.out.println("Flavor : ---------------" +flavor);
		return flavor.getId();
	}

	private String createImage(){
		Payload<File> payload;
		File file = new File("C:/Shelly/SJSU/Sem1/Virtulization Technology/Lecture 3/cirros-0.3.4-x86_64-disk.img");

		payload = Payloads.create(file);
		Image image = os.images().create(Builders.image().name("Cirros x64").isPublic(true)
				.containerFormat(ContainerFormat.BARE).diskFormat(DiskFormat.RAW).build(),payload);

		System.out.println("created image : "+image);
		return image.getId();
	}

	private String getTenants(){
		Tenant tenant = os.identity().tenants().getByName("admin");
		System.out.println("Tenant : " +tenant);
		return tenant.getId();
	}

	private String createSubnetNetwork(){
		String tenantId = getTenants();
		Network network = os.networking().network()
				.create(Builders.network().name("ext_network").tenantId(tenantId).adminStateUp(true).build());

		Subnet subnet = os.networking().subnet().create(Builders.subnet()
				.name("ext_Network_Subnet")
				.networkId(network.getId())
				.tenantId(tenantId)
				.ipVersion(IPVersionType.V4)
				.cidr("192.168.0.0/24")
				.build());

		Port port = os.networking().port().create(Builders.port()
				.name("port-1")
				.networkId(network.getId())
				.fixedIp("192.168.0.101", subnet.getId())
				.build());


		Router router = os.networking().router().create(Builders.router()
				.name("ext_netRouter")
				.adminStateUp(true)
				.build());
		System.out.println("router : "+router);

		RouterInterface iface = os.networking().router()
				.attachInterface(router.getId(), AttachInterfaceType.SUBNET, subnet.getId());

		System.out.println("iface : "+iface);
		System.out.println("Subnet created : " +subnet);
		System.out.println("network is : "+network);

		return port.getId();
	}


	public Server createVM(String VMName){
		String flavorId = "cd4318a3-6302-4364-a25b-b7c828e88208";
		String imageId = "99330478-c96b-4fd3-a1f5-387f05eb566a";
		String networkPort = createSubnetNetwork();
		ServerCreate sc = Builders.server().name(VMName).flavor(flavorId).image(imageId).addNetworkPort(networkPort).build();

		Server server = os.compute().servers().boot(sc);
		System.out.println("VM created : "+server);
		return server;
	}

	public List<? extends Server> getServers(){
		List<? extends Server> servers = os.compute().servers().list();
		for(Server ser: servers){
			System.out.println("server : "+ser);
		}
		return servers;
	}
	
	public void downloadFile(){
		List<? extends SwiftObject> objects = os.objectStorage().objects().list("myfiles");
		for (SwiftObject swiftObject : objects) { 
			DLPayload payload = swiftObject.download();
			System.out.println(payload);
		}
	}
	
	public File downloadFile(String fileName){
		List<? extends SwiftObject> objs = os.objectStorage().objects().list("Container1");
		DLPayload payload = null;
		File file = new File(fileName);
		try{
		for (SwiftObject swiftObject : objs) {
			if(fileName.equals(swiftObject.getName())){
				payload = swiftObject.download();
				payload.writeToFile(file);
				System.out.println(payload);
				break;
			}
		}}catch(Exception e){
			e.printStackTrace();
		}
		return file;
	}
	
	public File downloadFile(String fileName, String containerName){
		List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName);
		DLPayload payload = null;
		File file = new File(fileName);
		try{
		for (SwiftObject swiftObject : objs) {
			if(fileName.equals(swiftObject.getName())){
				payload = swiftObject.download();
				payload.writeToFile(file);
				System.out.println(payload);
				break;
			}
		}}catch(Exception e){
			e.printStackTrace();
		}
		return file;
	}
	
	public void uploadFile(File file, String fileName){
		//SwiftAccount account = os.objectStorage().account().get();
		String etag = os.objectStorage().objects().put("Container1", fileName, 
                Payloads.create(file)
                );
		System.out.println(etag);
	}
	
	public void uploadFile(File file, String fileName, String containerName){
		String etag = os.objectStorage().objects().put(containerName, fileName, 
                Payloads.create(file)
                );
		System.out.println(etag);
	}
	
	public List<? extends SwiftObject> getObjects(){
		List<? extends SwiftObject> objs = os.objectStorage().objects().list("Container1");
		return objs;
	}
	
	public List<? extends SwiftObject> getObjects(String containerName){
		List<? extends SwiftObject> objs = os.objectStorage().objects().list(containerName);
		return objs;
	}
	
	public List<? extends SwiftContainer> getContainers(){
		List<? extends SwiftContainer> objs = os.objectStorage().containers().list();
		return objs;
	}
	
	public void createContainer(String containerName){
		os.objectStorage().containers().create(containerName);
	}
	
	public User createUser(String userName, String password, String emailId){
		Tenant t = Builders.tenant().name(userName).description(userName + " Corporation Tenant").build();
		Tenant tenant = os.identity().tenants().create(t);
		Role memberRole = os.identity().roles().getByName("admin");
		User user = os.identity().users()
	              .create(Builders.user()
	                                .name(userName)
	                                .password(password)
	                                .email(emailId)
	                                .enabled(true)
	                                .tenant(tenant).build());
		os.identity().roles().addUserRole(tenant.getId(), user.getId(), memberRole.getId());
		return user;
	}
	
	public Statistics getObjectOutgoingBytes(){
		SampleCriteria criteria = new SampleCriteria();
		criteria.add("user", Oper.EQUALS, "de79672e9ced46d5a65bc1b273266977");
/*		criteria.add("end", Oper.EQUALS, new Date().getTime() - (2 * 15 * 60 * 60 * 1000));
		criteria.add("start", Oper.EQUALS, new Date().getTime() - (7 * 24 * 60 * 60 * 1000));*/
		//criteria.add("start", Oper.EQUALS, new Date().getTime());
		List<? extends Statistics> instanceStats = os.telemetry().meters().statistics("storage.objects.outgoing.bytes", criteria);
		//List<? extends Statistics> requestStats = os.telemetry().meters().statistics("storage.api.request", criteria);
	/*	for (Statistics statistics : requestStats) {
			System.out.println(statistics);
		}*/
		for (Statistics statistics : instanceStats) {
			System.out.println(statistics);
			return statistics;
		}
		return null;
	}
	
	public Statistics getObjectIncomingBytes(){
		SampleCriteria criteria = new SampleCriteria();
		criteria.add("user", Oper.EQUALS, "de79672e9ced46d5a65bc1b273266977");
		//criteria.add("date_options", Oper.EQUALS, 7);
	/*	criteria.add("end", Oper.EQUALS, new Date().getTime() - (2 * 15 * 60 * 60 * 1000));
		criteria.add("start", Oper.EQUALS, new Date().getTime() - (7 * 24 * 60 * 60 * 1000));*/
		//criteria.add("start", Oper.EQUALS, new Date().getTime());
		List<? extends Statistics> instanceStats = os.telemetry().meters().statistics("storage.objects.incoming.bytes"/*, criteria*/);
		//List<? extends Statistics> requestStats = os.telemetry().meters().statistics("storage.api.request", criteria);
	/*	for (Statistics statistics : requestStats) {
			System.out.println(statistics);
		}*/
		for (Statistics statistics : instanceStats) {
			System.out.println(statistics);
			System.out.printf("max : %.0f\n", statistics.getSum());
			return statistics;
		}
		return null;
	}
	
	public static void main(String args[]){
		OpenstackCompute obj = new OpenstackCompute("pavani", "pavani");
		//OpenstackCompute obj = new OpenstackCompute();
		//User user = obj.getUser("pavani");
		obj.getContainers();
		//obj.getObjectIncomingBytes();
		//obj.getObjectOutgoingBytes();
	}
}
