package ams;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class AccountRepositoryTest {

	public static void main(String[] args) throws IOException {
		AccountRepository repository = new FileAccountRepository();
//		String path = "account.dbf";
//		RandomAccessFile raf = new RandomAccessFile(path, "rw");
		
		// 파일에 등록 테스트
		
		((FileAccountRepository)repository).saveFile(new Account("1111-2222-3333", "김재훈", 1111, 10000));
		Calendar rentDate = Calendar.getInstance();
		//rentDate.set(2022,9, 1);
		((FileAccountRepository)repository).saveFile(new MinusAccount("2222-2222-3333", "김대출", 1234, 10000, 1000, rentDate));
		((FileAccountRepository)repository).saveFile(new Account("3333-2222-3333", "김재훈", 5555, 10000));
		int count = repository.getCount();
		System.out.println(count + "개의 계좌가 등록되었다..");
		
		// 전체 목록은 구현. 파일에 쓰인 목록 읽기?
		System.out.println("***** 등록된 모든 계좌 리스트(총 "+repository.getCount()+"개) *****");
		List<Account> list =  repository.getAccounts();
		
		for (Account account : list) {
			
			if(account instanceof MinusAccount) {
				MinusAccount ma =  (MinusAccount)account;
				System.out.println(ma.getAccountNumber() + "\t" + ma.getAccountOwner() + "\t" + ma.getPassword() + "\t" + ma.getRestMoney() + "\t" + ma.getRentMoney() + "\t" + String.format("%tF", ma.getRentDate()));	
			}else {
				System.out.println(account.getAccountNumber() + "\t" + account.getAccountOwner() + "\t" + account.getPassword() + "\t" + account.getRestMoney());
			}
		}
	}

}
