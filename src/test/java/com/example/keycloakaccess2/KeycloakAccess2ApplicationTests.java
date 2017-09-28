package com.example.keycloakaccess2;

import static java.util.Arrays.asList;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakAccess2ApplicationTests {

    private Keycloak kc = KeycloakBuilder.builder().serverUrl("http://localhost:8080/auth").realm("NAA")
            .username("Admin").password("Admin").clientId("product-app3")
            .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build()).build();

    @Test
    public void contextLoads() {
    }

    public void getAccessToken() {
        System.out.println("**********************Getting Access Token**********************");
        kc.tokenManager().getAccessTokenString();
        System.out
                .println("**********************************************************************************************************************************");
        System.out.println();
        System.out.println();
    }

    public String getCreatedId(Response response) {

        URI location = response.getLocation();
        if (!response.getStatusInfo().equals(Status.CREATED)) {
            StatusType statusInfo = response.getStatusInfo();
            throw new WebApplicationException("Create method returned status " + statusInfo.getReasonPhrase()
                    + " (Code: " + statusInfo.getStatusCode() + "); expected status: Created (201)", response);
        }
        if (location == null) {
            return null;
        }
        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    
    public void createUser() {

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("test123");
        credential.setTemporary(false);

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername("Test");
        newUser.setEmail("Test@Test.com");
        newUser.setEnabled(true);
        newUser.setCredentials(asList(credential));

        String Old_ID = kc.realm("NAA").users().search("test").get(0).getId();
        kc.realm("NAA").users().delete(Old_ID);
        Response response = kc.realm("NAA").users().create(newUser);
        if (response.getStatus() != 201) {
            System.err.println("Couldn't create user.");
            System.exit(0);
        }
        System.out
                .println("******************************************  User Created  ******************************************");

        String ID = getCreatedId(response);

        // Adding the roles to the user
        // we need to pass the role name as a parameter to the method get()
        System.out
                .println("******************************************  Giving the user the admin role ******************************************");
        RoleRepresentation role = kc.realm("NAA").roles().get("Admin").toRepresentation();
        kc.realm("NAA").users().get(ID).roles().realmLevel().add(asList(role));

        // Adding a user to a group
        // we need to pass the group name as a parameter to the method get()
        // This will give the user the manager role also
        System.out
                .println("******************************************  ADDING THE USER TO THE GROUP MANAGER ******************************************");
        kc.realm("NAA").users().get(ID).joinGroup("36175920-0b30-457d-8824-095fa2650e4c");

        // Removing the Role Admin role from the User.

        RoleRepresentation role1 = kc.realm("NAA").roles().get("Admin").toRepresentation();
        RoleRepresentation role2 = kc.realm("NAA").roles().get("Manager").toRepresentation();

        kc.realm("NAA").users().get(ID).roles().realmLevel().remove(asList(role1));
        kc.realm("NAA").users().get(ID).roles().realmLevel().remove(asList(role2));
        // Removing the user from the group Admin
        kc.realm("NAA").users().get(ID).leaveGroup("36175920-0b30-457d-8824-095fa2650e4c");
        kc.realm("NAA").users().get(ID).leaveGroup("58b03b9f-bcdd-4d3e-a470-0fee0339a9af");

        response.close();

    }

    public void createTestRole() throws Exception {
        // !!!!!!!!!!!!!!!! IMPORTANT: Must Delete manually the rolefrom keycloak server.
        // There is no adequate way to do that using the API since we will have to fill
        // all the JSONproperties of RoleRepresentation including the ID..

        System.out.println("**********************Creating a Test Role**********************");

        RoleRepresentation newRole = new RoleRepresentation();
        newRole.setId("Tester");
        newRole.setDescription("Testing softwares");
        newRole.setName("SWTester");

        kc.realm("NAA").roles().create(newRole);

        System.out
                .println("**********************************************************************************************************************************");
        System.out.println();
        System.out.println();

    }

    public void countUsers() {
        System.out.println("**********************Counting Users and getting Realm ID**********************");

        RealmResource realmsResource = kc.realm("NAA");
        RealmRepresentation rp = realmsResource.toRepresentation();
        String id = rp.getId();
        System.out.println("**************************************");
        System.out.println("Id => " + id);
        System.out.println("*************************************");

        UsersResource userResources = kc.realm("NAA").users();
        int userCount = userResources.count();
        System.out.println("****************************");
        System.out.println("user count => " + userCount);
        System.out.println("*****************************");
        System.out
                .println("**********************************************************************************************************************************");
        System.out.println();
        System.out.println();
    }

    public void displayRoles() {
        System.out.println("**********************Displaying all the roles**********************");

        for (RoleRepresentation r : kc.realm("NAA").roles().list()) {
            System.out.println("**************************************");
            System.out.println("Role Name => " + r.getName());
            System.out.println("Role ID => " + r.getId());
            System.out.println("Realm ID => " + r.getContainerId());

            System.out.println("**************************************");
            System.out.println();
        }
        System.out
                .println("**********************************************************************************************************************************");
        System.out.println();
        System.out.println();
    }

    public void displayUser() {

        List<UserRepresentation> list = kc.realms().realm("NAA").users().search("Admin");
        for (UserRepresentation r : list) {
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println(r.getId());
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
            System.out.println("******************************************************************");
        }

    }

    @Test
    public void displayAllUsers2() {
        Map<String, List<String>> users = new HashMap<>();
        List<UserRepresentation> Users = kc.realm("NAA").groups().group("bfd396f0-0016-48db-aa89-710518e2a731")
                .members();

        for (UserRepresentation u : Users) {

            List<RoleRepresentation> Roles = kc.realm("NAA").users().get(u.getId()).roles().realmLevel()
                    .listEffective();

            List<String> r = new LinkedList<>();

            for (RoleRepresentation role : Roles) {
                r.add(role.getName());
            }

            users.put(u.getUsername(), r);

        }
        System.out.println();
        System.out.println();

        for(Map.Entry<String,List<String>> map : users.entrySet())
        {
            System.out.print("USER = "+map.getKey()+"        Roles = ");
            for(String rr:map.getValue())
            {
                System.out.print(rr+" ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
        System.out.println();

    }

}
