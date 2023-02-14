package ams;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileAccountRepository implements AccountRepository {

	private static final String FILE_PATH = "account.dbf";

	private static final int RECORD_COUNT_LENGTH = 4;

	private static final int ACCOUNT_TYPE = 4;
	private static final int NUMBER_LENGTH = 32;
	private static final int OWNER_LENGTH = 10;
	private static final int PWD_LENGTH = 4;
	private static final int REST_MONEY_LENGTH = 8;
	private static final int RENT_MONEY_LENGTH = 8;
	private static final int RENT_DATE_LENGTH = 8;

	// 총 레코드 사이즈 : 74바이트
	private static final int RECORD_LENGTH = ACCOUNT_TYPE + NUMBER_LENGTH + OWNER_LENGTH + PWD_LENGTH
			+ REST_MONEY_LENGTH + RENT_MONEY_LENGTH + RENT_DATE_LENGTH;


	private RandomAccessFile file;
	
	private int recordCount = 0;

	public FileAccountRepository() throws IOException {
		file = new RandomAccessFile(FILE_PATH, "rw");
		if (file.length() != 0) {
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

		String accountNumber = account.getAccountNumber(); // file.writeUTF(name); 이거 왜 못쓰나? 친구 이름은 10바이트로 저장하기로 했기에,걍
															// wireUTF고 하면 몇바이트인지 알 수가 없음. 62바이트 구조가 무너진다.
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

		// 예금주명
		charCount = accountOwner.length();
		for (int i = 0; i < (OWNER_LENGTH / 2); i++) {
			file.writeChar((i < charCount ? accountOwner.charAt(i) : ' '));
		}

		// 비밀번호
		file.writeInt(password);
		// 잔액
		file.writeLong(restMoney);
		// 대출금액
		file.writeLong(rentMoney);
		// 대출일자
		file.writeLong(rentDate);
		file.seek(0);
		file.writeInt(++recordCount);
	}

	// 전체 계좌 조회
	// 밑에 read에서 바이트 코드로 account세트를 완성하고 그걸 리턴한다. 
	// 그럼 그걸 getAccounts가 받아서 account로 저장하고 그걸 리스트에 하나씩 인덱스로 넣는다. for문 돌려서 다 넣고 그 리스트 자체를 리턴한다.
	@Override
	public List<Account> getAccounts() throws IOException {
		List<Account> list = new ArrayList<Account>();
		for (int i = 0; i < recordCount; i++) {
			Account account = read(i);
			list.add(account);
		}
		return list;
	}

	/** 특정 위치의 레코드 정보를 Account로 반환. getAccounts 헬퍼 메소드. */
	private Account read(int index) throws IOException {

		Account account = null;

		String accountNumber = "";
		String accountOwner = "";
		int password = 0;
		long restMoney = 0;

		int accountType = 0; // 입출금계좌
		long rentMoney = 0;
		long rentDate = 0;

		// 로직 시작
		file.seek((index * RECORD_LENGTH) + RECORD_COUNT_LENGTH);
		accountType = file.readInt();
		
		for (int i = 0; i < (NUMBER_LENGTH / 2); i++) {
			accountNumber += file.readChar();
		}
		accountNumber = accountNumber.trim(); // 끝에 공백 제거.

		for (int i = 0; i < (OWNER_LENGTH / 2); i++) {
			accountOwner += file.readChar();
		}
		accountOwner = accountOwner.trim();
		password = file.readInt();
		restMoney = file.readLong();
		rentMoney = file.readLong();
		rentDate = file.readLong();

		// 이 부분에서 그냥 어카운트와 마이너스 어카운트 분기가 안 됨. 어카운트 타입 필드는 어디서 쓰이지? => 했다.
		if (accountType == 2) {
			Calendar today = Calendar.getInstance();
			today.setTimeInMillis(rentDate);
			account = new MinusAccount(accountNumber, accountOwner, password, restMoney, rentMoney, today);
		} else if (accountType == 1){
			account = new Account(accountNumber, accountOwner, password, restMoney);
		}
		return account;
	}
	
	@Override
	public Account findByNumber(String number) throws IOException {

		return null; // 먼저 컴파일 에러 안 되게 리턴값 쓰고 초기화 시키기.
	}

	@Override
	public List<Account> findByName(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAccount(String number) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}



}
