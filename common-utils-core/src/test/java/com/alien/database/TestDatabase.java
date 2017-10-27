package com.alien.database;

import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alien.database.entities.Person;
import com.alien.utils.core.database.DbConfig;
import com.alien.utils.core.date.DateHelper;

// ignore: dont run incase oracle not installed!
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DbConfig.class)
public class TestDatabase {
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private final String SQL_INSERT = "insert into test_table1 (ID, FORENAME, SURNAME, CREATED) values (?, ?, ?, ?)";

    
    @Before
    public void setup(){
    	createTable();
    	insertRows();
    }
    
    
    private void createTable(){
 		jdbcTemplate.execute("create table test_table1 (id int, forename varchar(255), surname varchar(255), created date)");
 		
    }

    private void insertRows(){
    	
    	int count = 0;
    	
    	DateHelper dateHelper = new DateHelper();
    	
        Date today = new Date();
        Date tomorrow = dateHelper.getDatePlusDays(today, +1);
        Date yesterday = dateHelper.getDatePlusDays(today, -1);
        
    	count += jdbcTemplate.update(SQL_INSERT, new Object[] {1, "fred", "flintstone", today});     
    	count += jdbcTemplate.update(SQL_INSERT, new Object[] {2, "freddy", "kruger", tomorrow});     
    	count += jdbcTemplate.update(SQL_INSERT, new Object[] {3, "anna", "kruger", yesterday});     
        
        assertTrue(count > 0);
    }
    
    @Test
    public void getInteger(){
    	
    	int count = 0;
    	
    	count = jdbcTemplate.queryForObject("select count(*) from ccsowner.test_table1", Integer.class);     
        
        assertTrue(count > 0);
    }
    
    @Test
    public void getString(){
    	
    	String name = jdbcTemplate.queryForObject("select forename from test_table1 where id='2'", String.class);     
        
        assertTrue("freddy".equals(name));
    }
    
    @Test
    public void getRows(){
    	
    	String sql = "select * from test_table1 where id != ?";     
    	
        List<Person> people = jdbcTemplate.query(sql, new Object[] { "2" }, new RowMapper<Person>() {
            @Override
            public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            	Person person = new Person();
            	person.setId(rs.getInt("id"));
            	person.setForename(rs.getString("forename"));
            	person.setSurname(rs.getString("surname"));
            	person.setCreated(rs.getDate("created"));
                
                return person;
            }
        });
                
        assertTrue(people.size() == 2);
    }
    
    private void dropTable(){
 		jdbcTemplate.execute("drop table test_table1");
    }
    
    @After
    public void teardown(){
    	dropTable();
    }
}
