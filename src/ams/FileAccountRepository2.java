package ams;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileAccountRepository2 implements AccountRepository {
	private static final String FILE_PATH = "account.dbf";
	
	private static final int RECORD_COUNT_LENGTH = 4;

	private static final int ACCOUNT_TYPE = 4;
	private static final int NUMBER_LENGTH = 32;
	private static final int OWNER_LENGTH = 10;
	private static final int PWD_LENGTH = 4;
	private static final int REST_MONEY_LENGTH = 8;
	private static final int RENT_MONEY_LENGTH = 8;
	private static final int RENT_DATE_LENGTH = 8;
	
	private static final int RECORD_LENGTH = ACCOUNT_TYPE + NUMBER_LENGTH + OWNER_LENGTH 
			+ PWD_LENGTH + REST_MONEY_LENGTH + RENT_MONEY_LENGTH + RENT_DATE_LENGTH;
	
	private RandomAccessFile file;
	
	private int recordCount = 0;
	
	public FileAccountRepository2() throws IOException {
		file = new RandomAccessFile(FILE_PATH, "rw"); // 왜 여기서 객체를 만들지?
		if (file.length() != 0) { //
			recordCount = file.readInt(); // readInt(): 스트림에서 Int값을 읽는다.
		}
	}
	
	@Override
	public int getCount() {
		return recordCount;
	}
	
	@Override
	public void addAccount(Account account) throws IOException {

		
	}
	
	public void saveFile(Account account) throws IOException {
		// 파일의 마지막 위치로 파일 포인터 이동.
		file.seek((recordCount * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
		
		String accountNumber = account.getAccountNumber();
		String accountOwner = account.getAccountOwner();
		int password = account.getPassword();
		long restMoney = account.getRestMoney();
		
		int accountType = 1; // 입출금계좌
		long rentMoney = 0;
		long rentDate = 0;
		
		// 마이너스 계좌인 경우.
		if (account instanceof MinusAccount) {
			accountType = 2;
			rentMoney = ((MinusAccount) account).getRentMoney();
			rentDate = ((MinusAccount) account).getRentDate().getTimeInMillis();
		}
		
		// 계좌종류
		file.writeInt(accountType);
		// 계좌번호
		int charCount = accountNumber.length();
		for (int i = 0; i < (NUMBER_LENGTH / 2); i++) {
			file.writeChar((i < charCount ? accountNumber.charAt(i) : ' '));
		}
		
		
	}
	
	


}
